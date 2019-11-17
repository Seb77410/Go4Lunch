package com.application.seb.go4lunch.Model;

import java.util.ArrayList;

public class Restaurant {

    private String name;
    private String id;
    private ArrayList<String> userLikeList;

    // --- CONSTRUCTOR ---
    public Restaurant() {
    }

    public Restaurant(String name, String id) {
        this.name = name;
        this.id = id;
    }

    // --- GETTERS ---
    public String getName() {return name;}
    public String getId() {return id;}
    public ArrayList<String> getUserLikeList() {return userLikeList;}

    // --- SETTERS ---
    public void setName(String name) {this.name = name;}
    public void setId(String id) {this.id = id;}
    public void setUserLikeList(ArrayList<String> userLikeList) {this.userLikeList = userLikeList;}

}
