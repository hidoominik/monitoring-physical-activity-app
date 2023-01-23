package com.example.phisicalactivitymonitoringapp.pulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityAddPulseBinding;
import com.example.phisicalactivitymonitoringapp.shared.navigation.DrawerBaseActivity;
import com.example.phisicalactivitymonitoringapp.sleep.AddSleepActivity;
import com.example.phisicalactivitymonitoringapp.sleep.Sleep;
import com.example.phisicalactivitymonitoringapp.sleep.SleepActivity;
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

public class AddPulseActivity extends DrawerBaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private DatabaseReference pulses;

    private DatePicker date;
    private TimePicker time;
    private EditText pulse;

    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityAddPulseBinding.inflate(getLayoutInflater()).getRoot());

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        pulses = database.getReference("Pulses");

        date = findViewById(R.id.pulse_date_input);
        time = findViewById(R.id.pulse_time_input);
        pulse = findViewById(R.id.pulse_value_input);

        createButton = findViewById(R.id.add_pulse_button);
        createButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add_pulse_button) {
            addPulse();
        }
    }

    public void addPulse() {
        String pulse_value = pulse.getText().toString().trim();

        if (Integer.parseInt(pulse_value) < 20 || Integer.parseInt(pulse_value) > 400) {
            pulse.setError("Wrong pulse value");
            pulse.requestFocus();
            return;
        }

        String sleepDateValue = date.getDayOfMonth() + "-" + (date.getMonth() + 1)
                + "-" + date.getYear();

        String startTimeValue;
        if (time.getMinute() < 10)
            startTimeValue = time.getHour() + ":0" + time.getMinute();
        else
            startTimeValue = time.getHour() + ":" + time.getMinute();

        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            String key = pulses.push().getKey();
                            pulses.child(Objects.requireNonNull(key)).setValue(
                                            new Pulse(
                                                    key,
                                                    pulse_value,
                                                    sleepDateValue,
                                                    startTimeValue,
                                                    user.getUsername()))
                                    .addOnSuccessListener(databaseTaskSucceeded -> {
                                        Intent intent = new Intent(AddPulseActivity.this, PulseActivity.class);
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