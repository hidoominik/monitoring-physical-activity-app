package com.example.phisicalactivitymonitoringapp.pulse;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityShowPulseStatsBinding;
import com.example.phisicalactivitymonitoringapp.shared.navigation.DrawerBaseActivity;
import com.example.phisicalactivitymonitoringapp.workouts.Workout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

public class ShowPulseStatsActivity extends DrawerBaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference pulses;

    private TextView maxPulse;
    private TextView minPulse;
    private TextView averagePulse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityShowPulseStatsBinding.inflate(getLayoutInflater()).getRoot());

        pulses = FirebaseDatabase.getInstance().getReference("Pulses");
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        maxPulse = findViewById(R.id.max_pulse);
        minPulse = findViewById(R.id.min_pulse);
        averagePulse = findViewById(R.id.average_pulse);

        getStats();
    }

    public void getStats() {
        if (currentUser != null) {
            Query pulseQuery = pulses.orderByChild("username").equalTo(currentUser.getDisplayName());
            pulseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Pulse> pulseList = new ArrayList<>();
                    snapshot.getChildren().forEach(child -> {
                        if (child.getValue(Workout.class) != null) {
                            pulseList.add(child.getValue(Pulse.class));
                        }
                    });

                    if (pulseList.size() != 0) {
                        List<Double> pulseValueList = new ArrayList<>();
                        for (Pulse p : pulseList) {
                            pulseValueList.add(Double.valueOf(p.getValue()));
                        }

                        OptionalDouble average = pulseValueList.stream().mapToDouble(a -> a).average();

                        maxPulse.setText(String.valueOf(Collections.max(pulseValueList)));
                        minPulse.setText(String.valueOf(Collections.min(pulseValueList)));
                        averagePulse.setText(new DecimalFormat("0.00").format(
                                average.isPresent() ? average.getAsDouble() : 0));
                    }
                    else {
                        maxPulse.setText(R.string.no_data_info);
                        minPulse.setText(R.string.no_data_info);
                        averagePulse.setText(R.string.no_data_info);
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