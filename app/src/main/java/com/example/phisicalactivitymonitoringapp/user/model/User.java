package com.example.phisicalactivitymonitoringapp.user.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@IgnoreExtraProperties
public class User {

    String username;

    String email;

    String weight;

    String height;

    Map<String, Object> watchingUsers;

    Map<String, Object> watchedUsers;


    public User() {
    }

    public User(String username, String email, String weight, String height) {
        this.username = username;
        this.email = email;
        this.weight = weight;
        this.height = height;
        this.watchingUsers = new HashMap<>();
        this.watchedUsers = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Map<String, Object> getWatchingUsers() {
        return watchingUsers;
    }

    public void setWatchingUsers(Map<String, Object> watchingUsers) {
        this.watchingUsers = watchingUsers;
    }

    public Map<String, Object> getWatchedUsers() {
        return watchedUsers;
    }

    public void setWatchedUsers(Map<String, Object> watchedUsers) {
        this.watchedUsers = watchedUsers;
    }
}