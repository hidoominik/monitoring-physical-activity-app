package com.example.phisicalactivitymonitoringapp.workouts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityShowStatsBinding;
import com.example.phisicalactivitymonitoringapp.databinding.ActivitySubscribedUsersBinding;
import com.example.phisicalactivitymonitoringapp.shared.navigation.DrawerBaseActivity;
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
import java.util.Objects;

public class ShowStatsActivity extends DrawerBaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String anotherUserUsername;
    private DatabaseReference users;
    private DatabaseReference workouts;

    private TextView allWorkoutsNumber;
    private TextView firstWorkoutDate;
    private TextView lastWorkoutDate;
    private TextView longestWorkoutTime;

    private TextView allWorkoutsNumber2;
    private TextView firstWorkoutDate2;
    private TextView lastWorkoutDate2;
    private TextView longestWorkoutTime2;

    private TextView yourStats;

    private TextView allWorkoutsNumberLabel2;
    private TextView firstWorkoutDateLabel2;
    private TextView lastWorkoutDateLabel2;
    private TextView longestWorkoutTimeLabel2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityShowStatsBinding.inflate(getLayoutInflater()).getRoot());

        users = FirebaseDatabase.getInstance().getReference("Users");
        workouts = FirebaseDatabase.getInstance().getReference("Workouts");
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        anotherUserUsername = getIntent().getSerializableExtra("username") != null
                ? getIntent().getSerializableExtra("username").toString()
                : null;

        allWorkoutsNumber = findViewById(R.id.all_workouts);
        firstWorkoutDate = findViewById(R.id.first_workout);
        lastWorkoutDate = findViewById(R.id.last_workout);
        longestWorkoutTime = findViewById(R.id.max_workout_time);

        allWorkoutsNumber2 = findViewById(R.id.all_workouts2);
        firstWorkoutDate2 = findViewById(R.id.first_workout2);
        lastWorkoutDate2 = findViewById(R.id.last_workout2);
        longestWorkoutTime2 = findViewById(R.id.max_workout_time2);

        yourStats = findViewById(R.id.your_stats);

        allWorkoutsNumberLabel2 = findViewById(R.id.all_workouts_label2);
        firstWorkoutDateLabel2 = findViewById(R.id.first_workout_label2);
        lastWorkoutDateLabel2 = findViewById(R.id.last_workout_label2);
        longestWorkoutTimeLabel2 = findViewById(R.id.max_workout_time_label2);

        String usernameForShowUserDetails = anotherUserUsername != null ?
                anotherUserUsername : currentUser.getDisplayName();
        getStats(usernameForShowUserDetails, allWorkoutsNumber, firstWorkoutDate, lastWorkoutDate,
                longestWorkoutTime);

        if (anotherUserUsername == null ||
                Objects.equals(usernameForShowUserDetails, currentUser.getDisplayName())) {
            allWorkoutsNumber2.setVisibility(View.INVISIBLE);
            firstWorkoutDate2.setVisibility(View.INVISIBLE);
            lastWorkoutDate2.setVisibility(View.INVISIBLE);
            longestWorkoutTime2.setVisibility(View.INVISIBLE);

            yourStats.setVisibility(View.INVISIBLE);

            allWorkoutsNumberLabel2.setVisibility(View.INVISIBLE);
            firstWorkoutDateLabel2.setVisibility(View.INVISIBLE);
            lastWorkoutDateLabel2.setVisibility(View.INVISIBLE);
            longestWorkoutTimeLabel2.setVisibility(View.INVISIBLE);
        }
        else {
            usernameForShowUserDetails = currentUser.getDisplayName();
            getStats(usernameForShowUserDetails, allWorkoutsNumber2, firstWorkoutDate2,
                    lastWorkoutDate2, longestWorkoutTime2);
        }
    }

    public void getStats(String username, TextView allWorkouts, TextView firstWorkout,
                         TextView lastWorkout, TextView longestWorkout) {
        if (currentUser != null) {
            Query workoutQuery = workouts.orderByChild("username").equalTo(username);
            workoutQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Workout> workoutList = new ArrayList<>();
                    snapshot.getChildren().forEach(child -> {
                        if (child.getValue(Workout.class) != null) {
                            workoutList.add(child.getValue(Workout.class));
                        }
                    });

                    if (workoutList.size() != 0) {
                        workoutList.sort(Comparator.comparing(Workout::getDate));

                        allWorkouts.setText(String.valueOf(workoutList.size()));
                        firstWorkout.setText(String.valueOf(workoutList.get(0).getDate()));
                        lastWorkout.setText(
                                String.valueOf(workoutList.get(workoutList.size() - 1).getDate()));

                        List<Long> workoutsTime = new ArrayList<>();
                        for (Workout w : workoutList) {
                            LocalTime startTime = LocalTime.parse(w.getStartTime());
                            LocalTime endTime = LocalTime.parse(w.getEndTime());

                            long diffInMinutes = Duration.between(startTime, endTime).toMinutes();

                            if (startTime.compareTo(endTime) > 0)
                                diffInMinutes = Duration.between(startTime, LocalTime.parse("23:59")).toMinutes()
                                        + Duration.between(LocalTime.parse("00:00"), endTime).toMinutes()
                                        + Duration.between(LocalTime.parse("00:00"), LocalTime.parse("00:01")).toMinutes();

                            workoutsTime.add(diffInMinutes);
                        }

                        longestWorkout.setText(String.valueOf(Collections.max(workoutsTime)));
                    }
                    else {
                        allWorkouts.setText(R.string.no_data_info);
                        firstWorkout.setText(R.string.no_data_info);
                        lastWorkout.setText(R.string.no_data_info);
                        longestWorkout.setText(R.string.no_data_info);
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