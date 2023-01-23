package com.example.phisicalactivitymonitoringapp.sleep;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityShowSleepStatsBinding;
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
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

public class ShowSleepStatsActivity extends DrawerBaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference users;
    private DatabaseReference sleeps;

    private TextView maxSleep;
    private TextView minSleep;
    private TextView averageSleep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityShowSleepStatsBinding.inflate(getLayoutInflater()).getRoot());

        users = FirebaseDatabase.getInstance().getReference("Users");
        sleeps = FirebaseDatabase.getInstance().getReference("Sleeps");
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        maxSleep = findViewById(R.id.max_sleep);
        minSleep = findViewById(R.id.min_sleep);
        averageSleep = findViewById(R.id.average_sleep);

        getStats();
    }

    public void getStats() {
        if (currentUser != null) {
            Query sleepQuery = sleeps.orderByChild("username").equalTo(currentUser.getDisplayName());
            sleepQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Sleep> sleepList = new ArrayList<>();
                    snapshot.getChildren().forEach(child -> {
                        if (child.getValue(Workout.class) != null) {
                            sleepList.add(child.getValue(Sleep.class));
                        }
                    });

                    if (sleepList.size() != 0) {
                        List<Long> sleepTime = new ArrayList<>();
                        for (Sleep s : sleepList) {
                            LocalTime startTime = LocalTime.parse(s.getStartTime());
                            LocalTime endTime = LocalTime.parse(s.getEndTime());

                            long diffInMinutes = Duration.between(startTime, endTime).toMinutes();

                            if (startTime.compareTo(endTime) > 0)
                                diffInMinutes = Duration.between(startTime, LocalTime.parse("23:59")).toMinutes()
                                        + Duration.between(LocalTime.parse("00:00"), endTime).toMinutes()
                                        + Duration.between(LocalTime.parse("00:00"), LocalTime.parse("00:01")).toMinutes();

                            sleepTime.add(diffInMinutes);
                        }

                        OptionalDouble average = sleepTime.stream().mapToDouble(a -> a).average();

                        long max = Collections.max(sleepTime);
                        long min = Collections.min(sleepTime);

                        long averageVal = 0;
                        if (average.isPresent())
                            averageVal = Math.round(average.getAsDouble());

                        maxSleep.setText(new DecimalFormat("#").format(max / 60) + " h "
                                + new DecimalFormat("#").format(max % 60));

                        minSleep.setText(new DecimalFormat("#").format(min / 60) + " h "
                                + new DecimalFormat("#").format(min % 60));

                        averageSleep.setText(new DecimalFormat("#").format(averageVal / 60) + " h "
                                + new DecimalFormat("#").format(averageVal % 60));
                    }
                    else {
                        maxSleep.setText(R.string.no_data_info);
                        minSleep.setText(R.string.no_data_info);
                        averageSleep.setText(R.string.no_data_info);
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