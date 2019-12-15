package com.application.seb.go4lunch.api;

import com.application.seb.go4lunch.model.User;
import com.application.seb.go4lunch.utils.Constants;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class FireStoreUserRequest {

    // --- COLLECTION REFERENCE ---

    private static CollectionReference UsersCollection(){
        return FirebaseFirestore.getInstance().collection(Constants.USER_COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String currentDate) {
        User userToCreate = new User(uid, username, urlPicture, currentDate);
        return FireStoreUserRequest.UsersCollection().document(uid).set(userToCreate);
    }

    public static Task<Void> createUser(String uid, String username, String currentDate) {
        User userToCreate = new User(uid, username,currentDate);
        return FireStoreUserRequest.UsersCollection().document(uid).set(userToCreate);

    }


    // --- GET ---

    public static Task<QuerySnapshot> getUserCollection(){
        return FireStoreUserRequest.UsersCollection().get();
    }

    public static Task<DocumentSnapshot> getUser(String userId){
        return FireStoreUserRequest.UsersCollection().document(userId).get();
    }

    public static Task<QuerySnapshot> getFormatUsersList(){
        return FireStoreUserRequest.UsersCollection().orderBy(Constants.ALREADY_SUBSCRIBE_RESTAURANT, Query.Direction.DESCENDING).get();
    }

    // --- UPDATE ---

    public static void everyDayValuesUpdate(String userId, String currentDate){
        FireStoreUserRequest.UsersCollection().document(userId)
                .update(Constants.CURRENT_DATE, currentDate,Constants.ALREADY_SUBSCRIBE_RESTAURANT,false);
    }

    public static void updateUserSubscribeBoolean(String userId){
    FireStoreUserRequest.UsersCollection().document(userId).update(Constants.ALREADY_SUBSCRIBE_RESTAURANT, true);
    }

    public static void updateUserNotificationsBoolean(String userId, Boolean isChecked){
    FireStoreUserRequest.UsersCollection().document(userId).update(Constants.ABLE_NOTIFICATIONS, isChecked);
    }

}
