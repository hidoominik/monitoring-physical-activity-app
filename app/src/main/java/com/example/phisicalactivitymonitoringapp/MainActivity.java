package com.example.phisicalactivitymonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.phisicalactivitymonitoringapp.authorization.UserLoginActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.google.android.material.navigation.NavigationView;

//User do testowania
//email: chomczaq@gmail.com
//password: asdf77
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    protected void onStart() {
        super.onStart();
        if(!(AuthService.ifUserIsLoggedIn())){
            startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        if(getSupportActionBar() !=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: Trzeba pomyśleć jak byśmy chcieli to nav menu dodawać do innych aktywności, dziedziczymy chamsko po MainActivity, czy może jakiś lepszy sposob na wstrzykiwanie tego menu?
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
                    signOut();
                    break;
            }
            return true;
        });

    }

    private void signOut(){
        AuthService.signOut();
        finishAffinity();
        startActivity(getIntent());
    }

    @Override
    public void onClick(View view) {
//    Na pewno przyda się onclick ;)
    }
}
