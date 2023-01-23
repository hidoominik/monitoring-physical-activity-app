package com.example.phisicalactivitymonitoringapp.pulse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityPulseBinding;
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

import java.util.ArrayList;
import java.util.List;

public class PulseActivity extends DrawerBaseActivity implements View.OnClickListener {

    private List<Pulse> pulseList;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference pulses;

    RecyclerView rvPulses;

    PulseAdapter adapter;

    private Button addNewPulseDataButton;
    private Button showPulseStatsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityPulseBinding.inflate(getLayoutInflater()).getRoot());

        addNewPulseDataButton = findViewById(R.id.new_pulse_data_button);
        addNewPulseDataButton.setOnClickListener(this);

        showPulseStatsButton = findViewById(R.id.pulse_stats_button);
        showPulseStatsButton.setOnClickListener(this);

        pulseList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        pulses = database.getReference("Pulses");

        rvPulses = findViewById(R.id.rvPulse);

        adapter = new PulseAdapter(pulseList);

        rvPulses.setAdapter(adapter);
        createPulseList();

        rvPulses.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_pulse_data_button) {
            Intent intent = new Intent(PulseActivity.this, AddPulseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else if (v.getId() == R.id.pulse_stats_button) {
            Intent intent = new Intent(PulseActivity.this, ShowPulseStatsActivity.class);
            startActivity(intent);
        }
    }

    public void createPulseList() {
        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            Query pulseQuery = pulses.orderByChild("username").equalTo(user.getUsername());
                            pulseQuery.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    pulseList.clear();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        pulseList.add(ds.getValue(Pulse.class));
                                        adapter.setPulseList(pulseList);
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

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("TAG", error.getMessage());
                }
            });
        }
    }
}