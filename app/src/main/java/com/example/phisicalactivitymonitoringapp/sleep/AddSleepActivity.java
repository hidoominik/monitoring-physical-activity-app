package com.example.phisicalactivitymonitoringapp.sleep;

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

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityAddSleepBinding;
import com.example.phisicalactivitymonitoringapp.shared.navigation.DrawerBaseActivity;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AddSleepActivity extends DrawerBaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private DatabaseReference sleeps;

    private DatePicker sleepDate;
    private TimePicker startTime;
    private TimePicker endTime;

    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityAddSleepBinding.inflate(getLayoutInflater()).getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        sleeps = database.getReference("Sleeps");

        sleepDate = findViewById(R.id.sleepDateInput);
        startTime = findViewById(R.id.startSleepTimeInput);
        endTime = findViewById(R.id.endSleepTimeInput);

        createButton = findViewById(R.id.addSleepButton);
        createButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addSleepButton) {
            addSleep();
        }
    }

    public void addSleep() {
        String sleepDateValue = sleepDate.getDayOfMonth() + "-" + (sleepDate.getMonth() + 1)
                + "-" + sleepDate.getYear();

        String startTimeValue;
        if (startTime.getMinute() < 10)
            startTimeValue = startTime.getHour() + ":0" + startTime.getMinute();
        else
            startTimeValue = startTime.getHour() + ":" + startTime.getMinute();

        String endTimeValue;
        if (endTime.getMinute() < 10)
            endTimeValue = endTime.getHour() + ":0" + endTime.getMinute();
        else
            endTimeValue = endTime.getHour() + ":" + endTime.getMinute();

        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            String key = sleeps.push().getKey();
                            sleeps.child(Objects.requireNonNull(key)).setValue(
                                            new Sleep(
                                                    key,
                                                    sleepDateValue,
                                                    startTimeValue,
                                                    endTimeValue,
                                                    user.getUsername()))
                                    .addOnSuccessListener(databaseTaskSucceeded -> {
                                        Intent intent = new Intent(AddSleepActivity.this, SleepActivity.class);
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
}