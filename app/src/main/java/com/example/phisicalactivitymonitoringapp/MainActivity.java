package com.example.phisicalactivitymonitoringapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.phisicalactivitymonitoringapp.authorization.UserLoginActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

//User do testowania
//email: chomczaq@gmail.com
//password: asdf77

//Konto google do testowania
//janrobak112@gmail.com
//robak112

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView stepNumber;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    public static final String TAG = "StepCounter";

    private final FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_LOCATION_SAMPLE, FitnessOptions.ACCESS_READ)
            .build();

    private List<String> theDates = new ArrayList<>();
    private List<Integer> totalAvgSteps = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!AuthService.ifUserIsLoggedIn()) {
            startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (hasRuntimePermissions()) {
                    if (!GoogleSignIn.hasPermissions(
                            GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
                        GoogleSignIn.requestPermissions(
                                this,
                                REQUEST_OAUTH_REQUEST_CODE,
                                GoogleSignIn.getLastSignedInAccount(this),
                                fitnessOptions);

                    } else {
                        subscribe();
                        readData();/**/
                    }
                } else {
                    requestRuntimePermissions();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        stepNumber = (TextView) findViewById(R.id.stepsNumber);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO: Trzeba pomyśleć jak byśmy chcieli to nav menu dodawać do innych aktywności,
        // dziedziczymy chamsko po MainActivity,
        // czy może jakiś lepszy sposob na wstrzykiwanie tego menu?
        //Navigation logic:
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    Log.i("MENU_DRAWER_TAG", "Home item clicked");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_search:
                    Log.i("MENU_DRAWER_TAG", "Search item clicked");
                    //logic for search
                    break;
                case R.id.nav_profile:
                    startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
                    break;
                case R.id.nav_add_workout:
                    startActivity(new Intent(MainActivity.this, AddWorkoutActivity.class));
                    break;
                case R.id.nav_logout:
                    Log.i("MENU_DRAWER_TAG", "Logout item clicked");
                    signOut();
                    break;
            }
            return true;
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Toast.makeText(this, "User interaction was cancelled",
                        Toast.LENGTH_SHORT).show();
                signOut();
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                signOut();
            }
        }
    }

    public void subscribe() {
        Fitness.getRecordingClient(this, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)))
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Successfully subscribed!",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "There was a problem subscribing.", task.getException());
                                Toast.makeText(MainActivity.this, "There was a problem subscribing.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void readData() {
        Fitness.getHistoryClient(this, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        dataSet -> {
                            long total =
                                    dataSet.isEmpty()
                                            ? 0
                                            : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                            theDates = new ArrayList<>();
                            totalAvgSteps = new ArrayList<>();
                            invokeHistoryApiForWeeklySteps();

                            if (dataSet.isEmpty())
                                stepNumber.setText("0");
                            else {
                                stepNumber.setText("Today total steps: " + total);
                            }
                        })
                .addOnFailureListener(
                        e -> Toast.makeText(MainActivity.this,
                                "There was a problem getting the step count." + e,
                                Toast.LENGTH_SHORT).show());
    }

    private boolean hasRuntimePermissions() {
        int ifGrantedAccessFineLocation = ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int ifGrantedBodySensors = ActivityCompat.
                checkSelfPermission(this, Manifest.permission.BODY_SENSORS);
        int ifGrantedActivityRecognition = ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION);

        return ifGrantedAccessFineLocation == PackageManager.PERMISSION_GRANTED
                && ifGrantedBodySensors == PackageManager.PERMISSION_GRANTED
                && ifGrantedActivityRecognition == PackageManager.PERMISSION_GRANTED;
    }

    private void requestRuntimePermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.BODY_SENSORS)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACTIVITY_RECOGNITION);

        if (shouldProvideRationale) {
            Toast.makeText(this,
                    "Displaying permission rationale to provide additional context",
                    Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BODY_SENSORS,
                            Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Requesting permission",
                    Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BODY_SENSORS,
                            Manifest.permission.ACTIVITY_RECOGNITION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void signOut() {
        AuthService.signOut();
        finishAffinity();
        startActivity(getIntent());
    }

    @Override
    public void onClick(View view) {
//    Na pewno przyda się onclick ;)
    }

    public void invokeHistoryApiForWeeklySteps() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long endTime = cal.getTimeInMillis();

        cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        long startTime = cal.getTimeInMillis();

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness
                .getHistoryClient(this, GoogleSignIn
                        .getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener(response -> {

                    for (Bucket bucket : response.getBuckets()) {
                        long days = bucket.getStartTime(TimeUnit.MILLISECONDS);
                        Date stepsDate = new Date(days);
                        @SuppressLint("SimpleDateFormat")
                        DateFormat df = new SimpleDateFormat("EEE");
                        String weekday = df.format(stepsDate);

                        theDates.add(weekday);
                        bucket.getDataSets().forEach(dataSet ->
                                totalAvgSteps.add(dumpDataSet(dataSet)));
                    }
                    Log.i(TAG, theDates.toString());
                    Log.i(TAG, totalAvgSteps.toString());

                    drawBarChart();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this,
                                "There was an error reading data from Google Fit" + e,
                                Toast.LENGTH_SHORT).show());

    }

    public int dumpDataSet(DataSet dataSet) {
        int totalSteps = 0;
        for (DataPoint dp : dataSet.getDataPoints())
            for (Field field : dp.getDataType().getFields())
                totalSteps += dp.getValue(field).asInt();

        return totalSteps;
    }

    public void drawBarChart() {
        BarChart chart = findViewById(R.id.chart);

        chart.invalidate();
        chart.clear();

        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(theDates));
        xAxis.setLabelCount(7, true);

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < theDates.size(); i++) {
            entries.add(new BarEntry(i, totalAvgSteps.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Data");
        BarData data = new BarData(dataSet);
        chart.setData(data);

        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        chart.getDescription().setEnabled(false);
    }
}
