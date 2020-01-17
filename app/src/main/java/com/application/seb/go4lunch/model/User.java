package com.application.seb.go4lunch.model;

public class User {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    private String uid;
    private String username;
    private String urlPicture;
    private Boolean alreadySubscribeRestaurant = false;
    private String currentDate;
    private boolean ableNotifications = true;
    private String subscribeRestaurant = null;

    //----------------------------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------------------------
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
    //----------------------------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------------------------
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getAlreadySubscribeRestaurant() {return alreadySubscribeRestaurant;}
    public String getCurrentDate() {return currentDate;}
    public boolean isAbleNotifications() {return ableNotifications;}
    public String getSubscribeRestaurant() {return subscribeRestaurant;}

    //----------------------------------------------------------------------------------------------
    // USetters
    //----------------------------------------------------------------------------------------------
    public void setCurrentDate(String currentDate) {this.currentDate = currentDate;}
}
