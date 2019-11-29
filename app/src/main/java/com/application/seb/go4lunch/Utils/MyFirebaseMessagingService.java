package com.application.seb.go4lunch.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.application.seb.go4lunch.API.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.API.FireStoreUserRequest;
import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.application.seb.go4lunch.Model.User;
import com.application.seb.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
        Log.e("Firebase messaging", "Receive");
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
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body));

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
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    if(Objects.requireNonNull(user).isAbleNotifications()){
                        getUserSubscribedRestaurant();
                    }
                    Log.e("Notifications", "getUserInfo()");
                });
    }

    private void getUserSubscribedRestaurant(){
        Log.e("Notifications", "getUserSubscribedRestaurant()");
        // Get every restaurant
        FireStoreRestaurantRequest
                .getRestaurantsCollection()
                .get()
                .addOnCompleteListener(task -> {
                    Log.e("Notifications", "getUserSubscribedRestaurant() onComplete()");
                    // For every restaurant : get Subscribers document for current date
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        FireStoreRestaurantRequest
                                .getRestaurantSubscribersCollection(Objects.requireNonNull(restaurant).getId())
                                .document(currentDate)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Log.e("Notifications", "getUserSubscribedRestaurant() onSuccess() for restaurant Subscribers document");
                                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);
                                    // If the document of current date have a subscribers list wich contains current user :
                                    if (subscribersCollection != null && subscribersCollection.getSubscribersList().contains(user.getUid())){
                                        restaurantName = restaurant.getName();
                                        restaurantAddress = restaurant.getAddress();
                                        restaurantSubscribersId = subscribersCollection.getSubscribersList();
                                        restaurantSubscribersId.remove(user.getUid());
                                        Log.e("Notifications", "Le retau : " + restaurantName + " Son adresse : " + restaurantAddress + " Ses subscribers : " + restaurantSubscribersId);
                                        // If
                                        if (restaurantSubscribersId.size()>0){
                                        for (int x = 0; x < restaurantSubscribersId.size(); x++){
                                                int finalX = x;
                                                FireStoreUserRequest
                                                        .getUsersCollection()
                                                        .document(restaurantSubscribersId.get(x))
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot1 -> {
                                                            User user = documentSnapshot1.toObject(User.class);
                                                            restaurantSubscribersName.add(Objects.requireNonNull(user).getUsername());
                                                            if (finalX == restaurantSubscribersId.size()-1){
                                                                setNotificationMessage();
                                                                showNotification();
                                                            }
                                                        });
                                            }
                                        }else{
                                            setNotificationMessage();
                                            showNotification();
                                        }
                                    }else{
                                        Log.e("Notification", "Not subscribe this restaurant : " + restaurant.getName());
                                    }
                                });
                    }
                });
    }

    private void setNotificationMessage() {

        body = "Restaurant : " + restaurantName + "\nAddress :  " + restaurantAddress + "\nWorkmates : " + restaurantSubscribersName;
    }
}
