package com.application.seb.go4lunch.model;

import java.util.ArrayList;

public class SubscribersCollection {

    //----------------------------------------------------------------------------------------------
    // For dat
    //----------------------------------------------------------------------------------------------
    private String currentDate;
    private ArrayList<String> subscribersList;

    //----------------------------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------------------------
    public SubscribersCollection() {}

    public SubscribersCollection(String currentDate, ArrayList<String> subscribersList) {
        this.currentDate = currentDate;
        this.subscribersList = subscribersList;
    }


    //----------------------------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------------------------
    public String getCurrentDate() {
        return currentDate;
    }
    public ArrayList<String> getSubscribersList() {
        return subscribersList;
    }

    //----------------------------------------------------------------------------------------------
    // Setters
    //----------------------------------------------------------------------------------------------
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
