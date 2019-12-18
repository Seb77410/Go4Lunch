package com.application.seb.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.application.seb.go4lunch.api.FireStoreRestaurantRequest;
import com.application.seb.go4lunch.api.FireStoreUserRequest;
import com.application.seb.go4lunch.controller.SignInActivity;
import com.application.seb.go4lunch.model.Restaurant;
import com.application.seb.go4lunch.model.SubscribersCollection;
import com.application.seb.go4lunch.model.User;
import com.application.seb.go4lunch.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    private User user;
    private String currentDate = Helper.setCurrentDate(Calendar.getInstance());
    private String restaurantName;
    private String restaurantAddress;
    private ArrayList<String> restaurantSubscribersId = new ArrayList<>();
    private ArrayList<String> restaurantSubscribersName = new ArrayList<>();
    private String body;

    //----------------------------------------------------------------------------------------------
    // OnNewToken
    //----------------------------------------------------------------------------------------------
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("FirebaseMesagingService", "Refreshed token: " + token);
    }

    //----------------------------------------------------------------------------------------------
    // OnReceive
    //----------------------------------------------------------------------------------------------
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d("FireBase messaging", remoteMessage.getData().toString());
        getUserInfo();
    }

    //----------------------------------------------------------------------------------------------
    // For notifications
    //----------------------------------------------------------------------------------------------

    /**
     * This method create and show notification
     */
    private void showNotification() {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NOTIFICATIONS_TAG, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, Constants.NOTIFICATIONS_TAG);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SignInActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        notifBuilder
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_logo_go4lunch)
                .setAutoCancel(true)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setContentIntent(contentIntent);

        notificationManager.notify(Constants.NOTIFICATIONS_TAG,Constants.NOTIFICATIONS_ID, notifBuilder.build());
    }

    /**
     * This method get user information by executing FireStore request
     */
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

    /**
     * This method get user subscribe restaurant information and show this information
     * into notification
     */
    private void getUserSubscribedRestaurant(){
        Log.e("Notifications", "getUserSubscribedRestaurant()");
        // Get every restaurant
        FireStoreRestaurantRequest.getRestaurantsCollection()
                .addOnCompleteListener(task -> {
                    Log.e("Notifications", "getUserSubscribedRestaurant() onComplete()");

                    // For every restaurant : get Subscribers document for current date
                    for (DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        FireStoreRestaurantRequest.getSubscriberList(Objects.requireNonNull(restaurant).getId(), currentDate)
                                .addOnSuccessListener(documentSnapshot -> {
                                    Log.d("Notifications", "getUserSubscribedRestaurant() onSuccess() for restaurant Subscribers document");

                                    SubscribersCollection subscribersCollection = documentSnapshot.toObject(SubscribersCollection.class);
                                    // If the document of current date have a subscribers list that contains current user :
                                    if (subscribersCollection != null && subscribersCollection.getSubscribersList().contains(user.getUid())){
                                        restaurantName = restaurant.getName();
                                        restaurantAddress = restaurant.getAddress();
                                        restaurantSubscribersId = subscribersCollection.getSubscribersList();
                                        restaurantSubscribersId.remove(user.getUid());
                                        Log.d("Notifications", "Le retau : " + restaurantName + " Son adresse : " + restaurantAddress + " Ses subscribers : " + restaurantSubscribersId);
                                        getRestaurantSubscribersNames(restaurantSubscribersId);
                                    }else{
                                        Log.e("Notification", "Not subscribe this restaurant : " + restaurant.getName());
                                    }
                                });
                    }
                });
    }

    /**
     * This method get restaurant subscribers names list from a restaurant subscribers Ids list
     * and show this names inside the notification
     * @param restaurantSubscribersId a restaurant subscribers names list
     */
    private void getRestaurantSubscribersNames(ArrayList<String> restaurantSubscribersId){
        if (restaurantSubscribersId.size()>0){
            for (int x = 0; x < restaurantSubscribersId.size(); x++){
                int finalX = x;
                FireStoreUserRequest.getUser(restaurantSubscribersId.get(x))
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
    }

    /**
     * This method modify notification text
     */
    private void setNotificationMessage() {
        body = getString(R.string.restaurant) + restaurantName + "\n"
               + getString(R.string.address) + restaurantAddress + "\n"
               + getString(R.string.workmates) + restaurantSubscribersName;
    }
}
