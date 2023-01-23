package com.example.phisicalactivitymonitoringapp.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivitySleepBinding;
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

public class SleepActivity extends DrawerBaseActivity implements View.OnClickListener {

    private List<Sleep> sleepList;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;
    DatabaseReference sleeps;

    RecyclerView rvSleep;

    SleepAdapter adapter;

    private Button addNewSleepDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivitySleepBinding.inflate(getLayoutInflater()).getRoot());

        addNewSleepDataButton = findViewById(R.id.new_sleep_data_button);
        addNewSleepDataButton.setOnClickListener(this);

        sleepList = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        sleeps = database.getReference("Sleeps");

        rvSleep = findViewById(R.id.rvSleep);

        adapter = new SleepAdapter(sleepList);

        rvSleep.setAdapter(adapter);
        createSleepList();

        rvSleep.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.new_sleep_data_button) {
            Intent intent = new Intent(SleepActivity.this, AddSleepActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    public void createSleepList() {
        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            Query sleepQuery = sleeps.orderByChild("username").equalTo(user.getUsername());
                            sleepQuery.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    sleepList.clear();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        sleepList.add(ds.getValue(Sleep.class));
                                        adapter.setSleepListAndKeyList(sleepList);
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