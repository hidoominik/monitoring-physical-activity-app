package com.example.phisicalactivitymonitoringapp.authorization.services;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AuthService {
    public static boolean ifUserIsLoggedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
    }

    public static String getUserLogin(){
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
    }
}
