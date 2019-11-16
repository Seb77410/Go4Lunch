package com.application.seb.go4lunch.API;

import com.application.seb.go4lunch.Model.Restaurant;
import com.application.seb.go4lunch.Model.SubscribersCollection;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;

public class FireStoreRestaurantRequest {

    private static final String COLLECTION_RESTAURANT_NAME = "restaurants";
    private static final String COLLECTION_SUBSCRIBERS_NAME = "subscribers";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANT_NAME);
    }

    public static CollectionReference getRestaurantSubscribersCollection(String placeId){
        return FireStoreRestaurantRequest.getRestaurantsCollection().document(placeId).collection(COLLECTION_SUBSCRIBERS_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String name, String placeId) {
        Restaurant restaurantToCreate = new Restaurant(name, placeId);
        return FireStoreRestaurantRequest.getRestaurantsCollection().document(placeId).set(restaurantToCreate, SetOptions.merge());
    }

    public static Task<Void> createRestaurantSubscribersCollection(String placeId, String currentDate, ArrayList<String> subscribersList){
        SubscribersCollection subscribersCollection = new SubscribersCollection(currentDate, subscribersList);
        return FireStoreRestaurantRequest.getRestaurantSubscribersCollection(placeId).document(currentDate).set(subscribersCollection);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String placeId){
        return FireStoreRestaurantRequest.getRestaurantsCollection().document(placeId).get();
    }

    public static Task<DocumentSnapshot> getSubscriberList(String placeId, String dateList){
        return FireStoreRestaurantRequest.getRestaurantSubscribersCollection(placeId).document(dateList).get();
    }

    // --- UPDATE --

    public static Task<Void> updapteSubscribersList(String placeId, String dateList, ArrayList<String> subscribersList){
        return FireStoreRestaurantRequest.getRestaurantSubscribersCollection(placeId).document(dateList).update("subscribersList", subscribersList);
    }


}
