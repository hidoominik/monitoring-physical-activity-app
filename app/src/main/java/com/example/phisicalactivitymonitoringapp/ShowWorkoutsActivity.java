package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.user.UserListActivity;
import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.example.phisicalactivitymonitoringapp.workouts.Workout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowWorkoutsActivity extends AppCompatActivity {

    private List<Workout> workoutList;
    private List<String> keyList;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference workouts;

    RecyclerView rvWorkouts;

    WorkoutsAdapter adapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_workouts);

        workoutList = new ArrayList<>();
        keyList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        workouts = database.getReference("Workouts");

        rvWorkouts = (RecyclerView) findViewById(R.id.rvWorkouts);

        adapter = new WorkoutsAdapter(workoutList, keyList);

        rvWorkouts.setAdapter(adapter);
        createWorkoutsList();

        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
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

    public void createWorkoutsList() {
        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            Query workoutsQuery = workouts.orderByChild("username").equalTo(user.getUsername());
                            workoutsQuery.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    keyList.clear();
                                    workoutList.clear();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        keyList.add(Objects.requireNonNull(ds.getValue(Workout.class)).getKey());
                                        workoutList.add(ds.getValue(Workout.class));
                                        adapter.setWorkoutListAndKeyList(workoutList, keyList);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        AuthService.signOut();
        finishAffinity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(ShowWorkoutsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}