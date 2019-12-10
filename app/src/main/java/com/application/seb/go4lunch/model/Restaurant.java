package com.application.seb.go4lunch.model;

import java.util.ArrayList;

public class Restaurant {

    // --- FOR DATA ---
    private String name;
    private String id;
    private ArrayList<String> userLikeList;
    private String address;

    // --- CONSTRUCTOR ---
    public Restaurant() {
    }

    public Restaurant(String name, String id, String address) {
        this.name = name;
        this.id = id;
        this.address = address;
    }

    // --- GETTERS ---
    public String getName() {return name;}
    public String getId() {return id;}
    public ArrayList<String> getUserLikeList() {return userLikeList;}
    public String getAddress() {return address;}

    // --- SETTERS ---
    public void setName(String name) {this.name = name;}
    public void setId(String id) {this.id = id;}
    public void setUserLikeList(ArrayList<String> userLikeList) {this.userLikeList = userLikeList;}
    public void setAddress(String address) {this.address = address; }
}
