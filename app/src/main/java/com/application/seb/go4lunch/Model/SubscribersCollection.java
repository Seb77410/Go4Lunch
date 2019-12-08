package com.application.seb.go4lunch.Model;

import java.util.ArrayList;

public class SubscribersCollection {

    private String currentDate;
    private ArrayList<String> subscribersList;

    // --- CONSTRUCTOR ---

    public SubscribersCollection() {}

    public SubscribersCollection(String currentDate, ArrayList<String> subscribersList) {
        this.currentDate = currentDate;
        this.subscribersList = subscribersList;
    }


    // --- GETTERS ---
    public String getCurrentDate() {
        return currentDate;
    }
    public ArrayList<String> getSubscribersList() {
        return subscribersList;
    }

    // --- SETTERS ---
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
    public void setSubscribersList(ArrayList<String> subscribersList) { this.subscribersList = subscribersList; }
}
