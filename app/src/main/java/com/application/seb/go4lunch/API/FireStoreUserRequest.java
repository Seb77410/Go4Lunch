package com.application.seb.go4lunch.API;

import com.application.seb.go4lunch.Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireStoreUserRequest {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String currentDate) {
        User userToCreate = new User(uid, username, urlPicture, currentDate);
        return FireStoreUserRequest.getUsersCollection().document(uid).set(userToCreate);
    }

    public static Task<Void> createUser(String uid, String username, String currentDate) {
        User userToCreate = new User(uid, username,currentDate);
        return FireStoreUserRequest.getUsersCollection().document(uid).set(userToCreate);

    }


    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return FireStoreUserRequest.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return FireStoreUserRequest.getUsersCollection().document(uid).update("username", username);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return FireStoreUserRequest.getUsersCollection().document(uid).delete();
    }
}
