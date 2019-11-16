package com.application.seb.go4lunch.Model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class User {

    private String uid;
    private String username;
    private ArrayList<String> likedPlaceList;
    @Nullable
    private String subscribedRestaurant;
    @Nullable
    private String urlPicture;

    // --- CONSTRUCTOR ---
    public User() { }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    public User(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getSubscribedRestaurant() { return subscribedRestaurant; }
    public ArrayList<String> getLikedPlaceList() {return likedPlaceList;}

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setSubscribedRestaurant(String subscribedRestaurant) { this.subscribedRestaurant = subscribedRestaurant; }
    public void setLikedPlaceList(ArrayList<String> likedPlaceList) {this.likedPlaceList = likedPlaceList;}
}
