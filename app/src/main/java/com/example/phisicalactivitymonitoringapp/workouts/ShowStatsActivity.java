package com.example.phisicalactivitymonitoringapp.workouts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.phisicalactivitymonitoringapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShowStatsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String anotherUserUsername;
    private DatabaseReference users;
    private DatabaseReference workouts;

    private TextView allWorkoutsNumber;
    private TextView firstWorkoutDate;
    private TextView lastWorkoutDate;
    private TextView longestWorkoutTime;

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

        allWorkoutsNumber = (TextView) findViewById(R.id.all_workouts);
        firstWorkoutDate = (TextView) findViewById(R.id.first_workout);
        lastWorkoutDate = (TextView) findViewById(R.id.last_workout);
        longestWorkoutTime = (TextView) findViewById(R.id.max_workout_time);

        getStats();
    }

    public void getStats() {
        if (currentUser != null) {
            String usernameForShowUserDetails = anotherUserUsername != null ?
                    anotherUserUsername : currentUser.getDisplayName();

            Query workoutQuery = workouts.orderByChild("username").equalTo(usernameForShowUserDetails);
            workoutQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Workout> workoutList = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Workout workout = child.getValue(Workout.class);
                        if (workout != null) {
                            workoutList.add(child.getValue(Workout.class));
                        }
                    }

                    if (workoutList.size() != 0) {
                        workoutList.sort(Comparator.comparing(Workout::getDate));

                        allWorkoutsNumber.setText(String.valueOf(workoutList.size()));
                        firstWorkoutDate.setText(String.valueOf(workoutList.get(0).getDate()));
                        lastWorkoutDate.setText(
                                String.valueOf(workoutList.get(workoutList.size() - 1).getDate()));

                        List<Long> workoutsTime = new ArrayList<>();
                        for (Workout w : workoutList) {
                            LocalTime startTime = LocalTime.parse(w.getStartTime());
                            LocalTime endTime = LocalTime.parse(w.getEndTime());

                            long diffInMinutes = Duration.between(startTime, endTime).toMinutes();

                            workoutsTime.add(diffInMinutes);
                        }

                        longestWorkoutTime.setText(String.valueOf(Collections.max(workoutsTime)));
                    }
                    else {
                        allWorkoutsNumber.setText(R.string.no_data_info);
                        firstWorkoutDate.setText(R.string.no_data_info);
                        lastWorkoutDate.setText(R.string.no_data_info);
                        longestWorkoutTime.setText(R.string.no_data_info);
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