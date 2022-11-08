package com.example.phisicalactivitymonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phisicalactivitymonitoringapp.authorization.UserLoginActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;

//User do testowania
//email: chomczaq@gmail.com
//password: asdf77
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signOut = (Button) findViewById(R.id.signOutMainActivityButton);
        signOut.setOnClickListener(this);

        if(!(AuthService.ifUserIsLoggedIn())){
            startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signOutMainActivityButton:
                signOut();
                break;
        }
    }

    private void signOut(){
        AuthService.signOut();
        finishAffinity();
        startActivity(getIntent());
    }
}
