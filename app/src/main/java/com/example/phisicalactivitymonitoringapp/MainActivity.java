package com.example.phisicalactivitymonitoringapp;

import android.Manifest;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

//User do testowania
//email: chomczaq@gmail.com
//password: asdf77

//Konto google do testowania
//janrobak112@gmail.com
//robak112

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    TextView stepNumber;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    public static final String TAG = "StepCounter";

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
                    FitnessOptions fitnessOptions = FitnessOptions.builder().
                            addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE).
                            addDataType(DataType.TYPE_STEP_COUNT_DELTA).build();

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
                            Toast.makeText(MainActivity.this, "Total steps: " + total,
                                    Toast.LENGTH_SHORT).show();


                            if (dataSet.isEmpty())
                                stepNumber.setText("0");
                            else
                                stepNumber.setText("Total steps: " + total);
                        })
                .addOnFailureListener(
                        e -> Toast.makeText(MainActivity.this, "There was a problem getting the step count." + e,
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
}
