package com.application.seb.go4lunch.model;

import java.util.ArrayList;

public class Restaurant {

    //----------------------------------------------------------------------------------------------
    // For data
    //----------------------------------------------------------------------------------------------
    private String name;
    private String id;
    private ArrayList<String> userLikeList;
    private String address;

    //----------------------------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------------------------
    public Restaurant() {
    }

    public Restaurant(String name, String id, String address) {
        this.name = name;
        this.id = id;
        this.address = address;
    }

    //----------------------------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------------------------
    public String getName() {return name;}
    public String getId() {return id;}
    public ArrayList<String> getUserLikeList() {return userLikeList;}
    public String getAddress() {return address;}

    //----------------------------------------------------------------------------------------------
    // Setters
    //----------------------------------------------------------------------------------------------
    public void setName(String name) {this.name = name;}
    public void setId(String id) {this.id = id;}
}
