package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    private Button logoutButton;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //this make app crush NullPointerException


        //Navigation logic:
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:
                        Log.i("MENU_DRAWER_TAG","Home item clicked");
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_search:
                        Log.i("MENU_DRAWER_TAG","Search item clicked");
                        //logic for search
                        break;

                    case R.id.nav_logout:
                        Log.i("MENU_DRAWER_TAG","Logout item clicked");
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        break;
                }
                return true;
            }
        });


//        logoutButton =(Button) findViewById(R.id.signOut);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
//            }
//        });
    }
}