package com.example.phisicalactivitymonitoringapp.authorization.services;

import com.google.firebase.auth.FirebaseAuth;

public class AuthService {
    public static boolean ifUserIsLoggedIn(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static void signOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
