package com.example.phisicalactivitymonitoringapp.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.phisicalactivitymonitoringapp.AddWorkoutActivity;
import com.example.phisicalactivitymonitoringapp.DeleteAccountActivity;
import com.example.phisicalactivitymonitoringapp.EditDataActivity;
import com.example.phisicalactivitymonitoringapp.EditPasswordActivity;
import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.ShowWorkoutsActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Synchronized;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String anotherUserUsername;
    private TextView usernameTextView;
    private TextView weight;
    private TextView height;
    private Button deleteButton;
    private Button editButton;
    private Button editPasswordButton;
    private Button subscribeButtton;
    private Button unsubscribeButton;

    private DatabaseReference mDatabase;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        findViews();

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        anotherUserUsername = getIntent().getSerializableExtra("username") != null
                ? getIntent().getSerializableExtra("username").toString()
                : null;

        loadUserData();

        setView();

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.nav_open, R.string.nav_close);

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Log.i("MENU_DRAWER_TAG", "Home item clicked");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_search:
                    Log.i("MENU_DRAWER_TAG", "Search item clicked");
                    //logic for search
                    break;
                case R.id.nav_profile:
                    startActivity(new Intent(this, UserProfileActivity.class));
                    break;
                case R.id.nav_user_list:
                    startActivity(new Intent(this, UserListActivity.class));
                    break;
                case R.id.nav_add_workout:
                    startActivity(new Intent(this, AddWorkoutActivity.class));
                    break;
                case R.id.nav_show_workouts:
                    startActivity(new Intent(this, ShowWorkoutsActivity.class));
                    break;
                case R.id.nav_logout:
                    Log.i("MENU_DRAWER_TAG", "Logout item clicked");
                    signOut();
                    break;
            }
            return true;
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_to_delete_button) {
            startActivity(new Intent(this, DeleteAccountActivity.class));
        }
        else if (v.getId() == R.id.go_to_edit_button) {
            startActivity(new Intent(this, EditDataActivity.class));
        }
        else if (v.getId() == R.id.go_to_change_password_button) {
            startActivity(new Intent(this, EditPasswordActivity.class));
        }
        else if (v.getId() == R.id.userProfileSubscribeButton) {
            subscribeUser();
        }
        else if (v.getId() == R.id.userProfileUnsubscribeButton) {
            unsubscribeUser();
        }
    }

    private void subscribeUser() {

        if (anotherUserUsername!=null && !Objects.requireNonNull(currentUser.getDisplayName()).isEmpty()){
        Map<String, Object> updateWatchedUsersMap = new HashMap<>();
        updateWatchedUsersMap.put(anotherUserUsername, true);
        Map<String, Object> updateWatchingUsersMap = new HashMap<>();
        updateWatchingUsersMap.put(currentUser.getDisplayName(), true);

            mDatabase.child(currentUser.getDisplayName()).child("watchedUsers").updateChildren(updateWatchedUsersMap);
            mDatabase.child(anotherUserUsername).child("watchingUsers").updateChildren(updateWatchingUsersMap);

            subscribeButtton.setVisibility(View.GONE);
            unsubscribeButton.setVisibility(View.VISIBLE);
            unsubscribeButton.setOnClickListener(this);
            Toast.makeText(this, "User has been subscribed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Error during subscribing", Toast.LENGTH_SHORT).show();
        }
    }
    private void unsubscribeUser() {

        if (anotherUserUsername!=null && !Objects.requireNonNull(currentUser.getDisplayName()).isEmpty()){

            mDatabase.child(currentUser.getDisplayName()).child("watchedUsers").child(anotherUserUsername).removeValue();
            mDatabase.child(anotherUserUsername).child("watchingUsers").child(currentUser.getDisplayName()).removeValue();

            unsubscribeButton.setVisibility(View.GONE);
            subscribeButtton.setVisibility(View.VISIBLE);
            subscribeButtton.setOnClickListener(this);
            Toast.makeText(this, "User has been unsubscribed", Toast.LENGTH_SHORT).show();

        }
        else {
            Toast.makeText(this, "Error during unsubscribing", Toast.LENGTH_SHORT).show();
        }
    }

    private void setView(){
        if (anotherUserUsername != null){
            setUserViewDependsOfSubscription();
        }
        else{
            setCurrentUserProfileView();
        }
    }

    @Synchronized
    private void setUserViewDependsOfSubscription() {

    mDatabase.child(Objects.requireNonNull(currentUser.getDisplayName())).child("watchedUsers").child(anotherUserUsername).get().addOnCompleteListener(task -> {
        if (task.getResult().exists()) {
            unsubscribeButton.setVisibility(View.VISIBLE);
            unsubscribeButton.setOnClickListener(this);
        }
        else {
            subscribeButtton.setVisibility(View.VISIBLE);
            subscribeButtton.setOnClickListener(this);
        }

    });
    }

    private void setCurrentUserProfileView(){
        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        editPasswordButton.setVisibility(View.VISIBLE);
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        editPasswordButton.setOnClickListener(this);
    }

    private void signOut() {
        AuthService.signOut();
        finishAffinity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void hideButtons(){
        editButton.setVisibility(View.GONE);
        editPasswordButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        subscribeButtton.setVisibility(View.GONE);
        unsubscribeButton.setVisibility(View.GONE);
    }


    private void findViews() {
        usernameTextView = findViewById(R.id.username);
        weight = findViewById(R.id.weight_value);
        height = findViewById(R.id.height_value);
        deleteButton = findViewById(R.id.go_to_delete_button);
        editButton = findViewById(R.id.go_to_edit_button);
        editPasswordButton = findViewById(R.id.go_to_change_password_button);
        subscribeButtton = findViewById(R.id.userProfileSubscribeButton);
        unsubscribeButton = findViewById(R.id.userProfileUnsubscribeButton);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
    }

    private void loadUserData() {
        if (currentUser != null) {

            String usernameForShowUserDetails = anotherUserUsername !=null ? anotherUserUsername : currentUser.getEmail();

            Query emailQuery = mDatabase.orderByChild("username").equalTo(usernameForShowUserDetails);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {

                            usernameTextView.setText(user.getUsername());

                            if (user.getWeight() == null)
                                weight.setText("No data");
                            else
                                weight.setText(user.getWeight());

                            if (user.getWeight() == null)
                                height.setText("No data");
                            else
                                height.setText(user.getHeight());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("TAG", error.getMessage());
                }
            });
        }
    }
}