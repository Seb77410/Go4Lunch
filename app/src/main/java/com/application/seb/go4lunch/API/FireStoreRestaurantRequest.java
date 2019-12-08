package com.application.seb.go4lunch.API;

import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class FireStoreRestaurantRequest {

    // --- COLLECTION REFERENCE ---

    public static CollectionReference restaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(Constants.RESTAURANT_COLLECTION_NAME);
    }

    public static CollectionReference restaurantSubscribersCollection(String placeId){
        return FireStoreRestaurantRequest.restaurantsCollection().document(placeId).collection(Constants.SUBSCRIBERS_COLLECTION_NAME);
    }

    // --- CREATE ---

    public static void createRestaurant(String name, String placeId, String address) {
        Restaurant restaurantToCreate = new Restaurant(name, placeId, address);
        FireStoreRestaurantRequest.restaurantsCollection().document(placeId).set(restaurantToCreate, SetOptions.merge());
    }


    // --- GET ---

    public static Task<QuerySnapshot> getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(Constants.RESTAURANT_COLLECTION_NAME).get();
    }

    public static Task<DocumentSnapshot> getRestaurant(String placeId){
        return FireStoreRestaurantRequest.restaurantsCollection().document(placeId).get();
    }

    public static Task<DocumentSnapshot> getSubscriberList(String placeId, String dateList){
        return FireStoreRestaurantRequest.restaurantSubscribersCollection(placeId).document(dateList).get();
    }

    // --- UPDATE --

    public static Task<Void> updateSubscribersList(String placeId, String currentDate, HashMap<String, ArrayList<String>> data){
        return FireStoreRestaurantRequest.restaurantSubscribersCollection(placeId).document(currentDate).set(data, SetOptions.merge());
    }

    public static Task<Void> updatePlaceLikedList(String placeId, HashMap<String, ArrayList<String>> data){
        return FireStoreRestaurantRequest.restaurantsCollection().document(placeId).set(data, SetOptions.merge());
    }


}
