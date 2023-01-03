package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

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

public class AddWorkoutActivity extends AppCompatActivity implements View.OnClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.createButton) {
            addWorkout();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addWorkout() {
        String workoutNameValue = workoutName.getText().toString().trim();
        String workoutPlaceValue = workoutPlace.getText().toString().trim();
        String workoutDateValue = workoutDate.getDayOfMonth() + "-" + workoutDate.getMonth()
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
                                        startActivity(new Intent(AddWorkoutActivity.this, AddWorkoutActivity.class));
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
}