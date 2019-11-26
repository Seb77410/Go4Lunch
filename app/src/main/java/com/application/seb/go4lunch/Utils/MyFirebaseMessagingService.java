package com.application.seb.go4lunch.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String NOTIFICATIONS_TAG = "go4lunch";
    private int NOTIFICATIONS_CHANNEL_ID = 3;
    private User user;
    private String currentDate = Helper.setCurrentDate();
    private String restaurantName;
    private String restaurantAddress;
    private ArrayList<String> restaurantSubscribersId = new ArrayList<>();
    private ArrayList<String> restaurantSubscribersName = new ArrayList<>();
    private String body;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        getUserInfo();
    }

    private void showNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATIONS_TAG, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, NOTIFICATIONS_TAG);
        notifBuilder
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_logo_go4lunch)
                .setAutoCancel(true)
                .setContentText(body);

        notificationManager.notify(NOTIFICATIONS_TAG,NOTIFICATIONS_CHANNEL_ID, notifBuilder.build());


    }

    @Override
    public void onNewToken(String token) {
        Log.d("FirebaseMesagingService", "Refreshed token: " + token);
        //sendRegistrationToServer(token);
    }

    private void getUserInfo(){
        FireStoreUserRequest
                .getUser(FirebaseAuth.getInstance().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        getUserSubscribedRestaurant();
                    }
                });
    }

    private void getUserSubscribedRestaurant(){

        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot document : task.getResult()){
                                Restaurant restaurant = document.toObject(Restaurant.class);
                            FireStoreRestaurantRequest
                                    .getRestaurantSubscribersCollection(Objects.requireNonNull(restaurant).getId())
                                    .document(currentDate)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);

                                            if (subscribersCollection != null && subscribersCollection.getSubscribersList().contains(user.getUid())){
                                                restaurantName = restaurant.getName();
                                                restaurantAddress = restaurant.getAddress();
                                                restaurantSubscribersId = subscribersCollection.getSubscribersList();
                                                restaurantSubscribersId.remove(user.getUid());
                                                Log.e("Notifications", "Le retau : " + restaurantName + " Son adresse : " + restaurantAddress + " Ses subscribers : " + restaurantSubscribersId);
                                                    for (int x = 0; x < restaurantSubscribersId.size(); x++){
                                                        int finalX = x;
                                                        FireStoreUserRequest
                                                                .getUsersCollection()
                                                                .document(restaurantSubscribersId.get(x))
                                                                .get()
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        User user = documentSnapshot.toObject(User.class);
                                                                        restaurantSubscribersName.add(Objects.requireNonNull(user).getUsername());
                                                                        if (finalX == restaurantSubscribersId.size()-1){
                                                                            setNotificationMessage();
                                                                            showNotification();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                            }else{

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void setNotificationMessage() {

        body = restaurantName + " " + restaurantAddress + " " + restaurantSubscribersName;
    }
}
