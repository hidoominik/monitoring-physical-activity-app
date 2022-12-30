package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.example.phisicalactivitymonitoringapp.workouts.Workout;
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

public class ShowWorkoutsActivity extends AppCompatActivity {

    private List<Workout> workoutList;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference workouts;

    WorkoutsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_workouts);

        workoutList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        workouts = database.getReference("Workouts");

        RecyclerView rvWorkouts = (RecyclerView) findViewById(R.id.rvWorkouts);

        adapter = new WorkoutsAdapter(workoutList);

        rvWorkouts.setAdapter(adapter);
        createWorkoutsList();

        rvWorkouts.setLayoutManager(new LinearLayoutManager(this));
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
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        workoutList.add(ds.getValue(Workout.class));
                                        adapter.setWorkoutList(workoutList);
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
}