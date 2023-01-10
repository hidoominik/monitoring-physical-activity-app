package com.example.phisicalactivitymonitoringapp.workouts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.phisicalactivitymonitoringapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ShowStatsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String anotherUserUsername;
    private DatabaseReference users;
    private DatabaseReference workouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stats);

        users = FirebaseDatabase.getInstance().getReference("Users");
        workouts = FirebaseDatabase.getInstance().getReference("Workouts");
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        anotherUserUsername = getIntent().getSerializableExtra("username") != null
                ? getIntent().getSerializableExtra("username").toString()
                : null;

        getStats();
    }

    public void getStats() {
        if (currentUser != null) {
            String usernameForShowUserDetails = anotherUserUsername != null ?
                    anotherUserUsername : currentUser.getDisplayName();
            System.out.println("AAAAA " + usernameForShowUserDetails);
            Query workoutQuery = workouts.orderByChild("username").equalTo(usernameForShowUserDetails);

//            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            workoutQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Workout workout = child.getValue(Workout.class);
                        if (workout != null) {
                            System.out.println(workout.getName());
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