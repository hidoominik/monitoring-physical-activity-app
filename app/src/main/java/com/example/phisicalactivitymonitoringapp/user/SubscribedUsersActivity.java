package com.example.phisicalactivitymonitoringapp.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.adapters.UserDataAdapter;
import com.example.phisicalactivitymonitoringapp.user.adapters.ViewPagerAdapter;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.example.phisicalactivitymonitoringapp.workouts.Workout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SubscribedUsersActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_users);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        //</ get elements >
        DatabaseReference workoutsReference = users.child(Objects.requireNonNull(currentUser.getDisplayName()));
        workoutsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    Map<String, Object> watchedUsers = user.getWatchedUsers()!=null ? user.getWatchedUsers() : new HashMap<>();
                    Map<String, Object> watchingUsers = user.getWatchingUsers()!=null ? user.getWatchingUsers() : new HashMap<>();

                    ViewPagerAdapter adapter = new ViewPagerAdapter(SubscribedUsersActivity.this, watchedUsers.keySet(), watchingUsers.keySet());
                    viewPager2.setAdapter(adapter);


                    new TabLayoutMediator(tabLayout, viewPager2,
                            (tab, position) -> {
                                if(position == 0){
                                    tab.setText("Watched users");
                                }
                                else{
                                    tab.setText("Watching users");
                                }}).attach();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Data has not been loaded", error.getMessage());
            }
        });


    }
}