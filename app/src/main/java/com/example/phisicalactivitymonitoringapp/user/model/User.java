package com.example.phisicalactivitymonitoringapp.user.model;

import lombok.Data;

//TODO: Trzeba dorobić model usera, w zależności co chcemy żeby taki user posiadał
//      Na pewno będzie to login, email, może jakiś avatar (możnaby pewnie wykorzystać firebaseStoreDatabase)
//      Trzeba sie zastanowić co chcemy

@Data
public class User {
    public String username, email;
    public User(){

    }
    public User(String username, String email){
        this.username = username;
        this.email = email;

    }
}
