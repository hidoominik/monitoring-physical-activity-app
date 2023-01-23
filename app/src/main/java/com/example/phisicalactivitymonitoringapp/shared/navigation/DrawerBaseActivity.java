package com.example.phisicalactivitymonitoringapp.shared.navigation;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.phisicalactivitymonitoringapp.AddWorkoutActivity;
import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.ShowWorkoutsActivity;
import com.example.phisicalactivitymonitoringapp.authorization.UserLoginActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.sleep.SleepActivity;
import com.example.phisicalactivitymonitoringapp.user.SubscribedUsersActivity;
import com.example.phisicalactivitymonitoringapp.user.UserListActivity;
import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.example.phisicalactivitymonitoringapp.workouts.ShowStatsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DrawerBaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private StorageReference mStorage;

    private ImageFilterView profileImage;

    private TextView loginTV;

    @Override
    public void setContentView(View view){
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_base, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        navigationView = drawerLayout.findViewById(R.id.baseNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(view.getTag()!=null){
                getSupportActionBar().setTitle(view.getTag().toString());
            }
        }
        mStorage = FirebaseStorage.getInstance().getReference("Avatars").child(AuthService.getUserLogin() + ".png");

        View headerLayout = navigationView.getHeaderView(0);
        profileImage = headerLayout.findViewById(R.id.profileImage);
        loginTV = headerLayout.findViewById(R.id.username);

        loginTV.setText(AuthService.getUserLogin());

        mStorage.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
            Picasso.with(DrawerBaseActivity.this).load(downloadUrl.toString()).into(profileImage);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.nav_search:
                startActivity(new Intent(this, UserListActivity.class));
                finish();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(this, UserProfileActivity.class));
                finish();
                break;
            case R.id.nav_user_list:
                startActivity(new Intent(this, SubscribedUsersActivity.class));
                finish();
                break;
            case R.id.nav_add_workout:
                startActivity(new Intent(this, AddWorkoutActivity.class));
                finish();
                break;
            case R.id.nav_show_workouts:
                startActivity(new Intent(this, ShowWorkoutsActivity.class));
                finish();
                break;
            case R.id.nav_show_stats:
                startActivity(new Intent(this, ShowStatsActivity.class));
                finish();
                break;
            case R.id.nav_show_sleep:
                startActivity(new Intent(this, SleepActivity.class));
                finish();
                break;
            case R.id.nav_logout:
                signOut();
                break;
        }
        return false;
    }

    private void signOut() {
        AuthService.signOut();
        Intent intent = new Intent(this, UserLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finishAffinity();
        startActivity(intent);
    }
}