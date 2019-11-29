package com.application.seb.go4lunch.Model;

import androidx.annotation.Nullable;

import com.application.seb.go4lunch.Utils.Helper;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private Boolean alreadySubscribeRestaurant = false;
    private String currentDate;
    private boolean ableNotifications = true;

    // --- CONSTRUCTOR ---
    public User() { }

    public User(String uid, String username, String urlPicture, String currentDate) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.currentDate = currentDate;
    }

    public User(String uid, String username, String currentDate) {
        this.uid = uid;
        this.username = username;
        this.currentDate = currentDate;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getAlreadySubscribeRestaurant() {return alreadySubscribeRestaurant;}
    public String getCurrentDate() {return currentDate;}
    public boolean isAbleNotifications() {return ableNotifications;}

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setAlreadySubscribeRestaurant(Boolean alreadySubscribeRestaurant) {this.alreadySubscribeRestaurant = alreadySubscribeRestaurant;}
    public void setCurrentDate(String currentDate) {this.currentDate = currentDate;}
    public void setAbleNotifications(boolean ableNotifications) {this.ableNotifications = ableNotifications;}
}
