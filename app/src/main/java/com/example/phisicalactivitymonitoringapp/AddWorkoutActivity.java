package com.example.phisicalactivitymonitoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.phisicalactivitymonitoringapp.databinding.ActivityAddWorkoutBinding;
import com.example.phisicalactivitymonitoringapp.shared.navigation.DrawerBaseActivity;
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

import java.util.Objects;

public class AddWorkoutActivity extends DrawerBaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private DatabaseReference workouts;

    private EditText workoutName;
    private EditText workoutPlace;
    private DatePicker workoutDate;
    private TimePicker startTime;
    private TimePicker endTime;

    private TextView workoutNameLabel;
    private TextView workoutPlaceLabel;

    private Button createButton;
    private Button editButton;

    private String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityAddWorkoutBinding.inflate(getLayoutInflater()).getRoot());

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            key = extras.getString("key");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        workouts = database.getReference("Workouts");

        workoutName = findViewById(R.id.workoutNameInput);
        workoutPlace = findViewById(R.id.workoutPlaceInput);
        workoutDate = findViewById(R.id.workoutDateInput);
        startTime = findViewById(R.id.startTimeInput);
        endTime = findViewById(R.id.endTimeInput);

        workoutNameLabel = findViewById(R.id.workoutName);
        workoutPlaceLabel = findViewById(R.id.workoutPlace);

        createButton = findViewById(R.id.createButton);
        createButton.setOnClickListener(this);

        editButton = findViewById(R.id.edit_workout_button);
        editButton.setOnClickListener(this);

        if (key != null) {
            createButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);

            DatabaseReference workoutsReference = workouts.child(key);
            workoutsReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Workout workout = snapshot.getValue(Workout.class);
                    if (workout != null) {
                        workoutName.setText(workout.getName());
                        workoutPlace.setText(workout.getPlace());

                        String[] str = Objects.requireNonNull(workout.getDate()).split("-");
                        int day = Integer.parseInt(str[0]);
                        int month = Integer.parseInt(str[1]) - 1;
                        int year = Integer.parseInt(str[2]);

                        workoutDate.init(year, month, day, null);

                        String[] str2 = Objects.requireNonNull(workout.getStartTime()).split(":");
                        int hour = Integer.parseInt(str2[0]);
                        int minute = Integer.parseInt(str2[1]);

                        startTime.setCurrentHour(hour);
                        startTime.setCurrentMinute(minute);

                        String[] str3 = Objects.requireNonNull(workout.getEndTime()).split(":");
                        hour = Integer.parseInt(str3[0]);
                        minute = Integer.parseInt(str3[1]);

                        endTime.setCurrentHour(hour);
                        endTime.setCurrentMinute(minute);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("ERROR", "listener canceled", error.toException());
                }
            });
        }
        else {
            createButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.createButton) {
            addWorkout();
        }
        else if (view.getId() == R.id.edit_workout_button) {
            editData(key);
        }
    }

    public void addWorkout() {
        String workoutNameValue = workoutName.getText().toString().trim();
        String workoutPlaceValue = workoutPlace.getText().toString().trim();
        String workoutDateValue = workoutDate.getDayOfMonth() + "-" + (workoutDate.getMonth() + 1)
                + "-" + workoutDate.getYear();
        String startTimeValue = startTime.getHour() + ":" + startTime.getMinute();
        String endTimeValue = endTime.getHour() + ":" + endTime.getMinute();

        if (workoutNameValue.isEmpty()) {
            workoutNameLabel.setError("Workout name is required");
            workoutNameLabel.requestFocus();
            return;
        }

        if (workoutPlaceValue.isEmpty()) {
            workoutPlaceLabel.setError("Workout place is required");
            workoutPlaceLabel.requestFocus();
            return;
        }

        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            String key = workouts.push().getKey();
                            workouts.child(Objects.requireNonNull(key)).setValue(
                                            new Workout(
                                                    key,
                                                    workoutNameValue,
                                                    workoutPlaceValue,
                                                    workoutDateValue,
                                                    startTimeValue,
                                                    endTimeValue,
                                                    user.getUsername()))
                                    .addOnSuccessListener(databaseTaskSucceeded -> {
                                        Intent intent = new Intent(AddWorkoutActivity.this, AddWorkoutActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }).addOnFailureListener(databaseTaskFailed -> {
                                        //
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

    public void editData(String mKey) {
        String workoutNameValue = workoutName.getText().toString().trim();
        String workoutPlaceValue = workoutPlace.getText().toString().trim();
        String workoutDateValue = workoutDate.getDayOfMonth() + "-" + (workoutDate.getMonth() + 1)
                + "-" + workoutDate.getYear();
        String startTimeValue = startTime.getHour() + ":" + startTime.getMinute();
        String endTimeValue = endTime.getHour() + ":" + endTime.getMinute();

        if (workoutNameValue.isEmpty()) {
            workoutNameLabel.setError("Workout name is required");
            workoutNameLabel.requestFocus();
            return;
        }

        if (workoutPlaceValue.isEmpty()) {
            workoutPlaceLabel.setError("Workout place is required");
            workoutPlaceLabel.requestFocus();
            return;
        }

        DatabaseReference workoutReference = workouts.child(mKey);
        workoutReference.child("name").setValue(workoutNameValue);
        workoutReference.child("place").setValue(workoutPlaceValue);
        workoutReference.child("date").setValue(workoutDateValue);
        workoutReference.child("startTime").setValue(startTimeValue);
        workoutReference.child("endTime").setValue(endTimeValue);

        Intent intent = new Intent(this, ShowWorkoutsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}