package com.example.phisicalactivitymonitoringapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class UserProfileActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
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
        setContentView(R.layout.activity_user_profile);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(getSupportActionBar() !=null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Navigation logic:
        navigationView.setNavigationItemSelectedListener(item -> {
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
                    AuthService.signOut();
                    finishAffinity();
                    startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
                    break;
            }
            return true;
        });



    }
}