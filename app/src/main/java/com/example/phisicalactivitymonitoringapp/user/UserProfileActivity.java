package com.example.phisicalactivitymonitoringapp.user;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.phisicalactivitymonitoringapp.AddWorkoutActivity;
import com.example.phisicalactivitymonitoringapp.DeleteAccountActivity;
import com.example.phisicalactivitymonitoringapp.EditDataActivity;
import com.example.phisicalactivitymonitoringapp.EditPasswordActivity;
import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.ShowWorkoutsActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private TextView usernameTextView;
    private TextView weight;
    private TextView height;
    private Button deleteButton;
    private Button editButton;
    private Button editPasswordButton;

    private DatabaseReference mDatabase;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        usernameTextView = findViewById(R.id.username);
        weight = findViewById(R.id.weight_value);
        height = findViewById(R.id.height_value);
        deleteButton = findViewById(R.id.go_to_delete_button);
        editButton = findViewById(R.id.go_to_edit_button);
        editPasswordButton = findViewById(R.id.go_to_change_password_button);
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        editPasswordButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Query emailQuery = mDatabase.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {

                            usernameTextView.setText(user.getUsername());

                            if (user.getWeight() == null)
                                weight.setText("No data");
                            else
                                weight.setText(user.getWeight());

                            if (user.getWeight() == null)
                                height.setText("No data");
                            else
                                height.setText(user.getHeight());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("TAG", error.getMessage());
                }
            });
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.nav_open, R.string.nav_close);

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
                    startActivity(new Intent(this, UserProfileActivity.class));
                    break;
                case R.id.nav_user_list:
                    startActivity(new Intent(this, UserListActivity.class));
                    break;
                case R.id.nav_add_workout:
                    startActivity(new Intent(this, AddWorkoutActivity.class));
                    break;
                case R.id.nav_show_workouts:
                    startActivity(new Intent(this, ShowWorkoutsActivity.class));
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_to_delete_button) {
            startActivity(new Intent(this, DeleteAccountActivity.class));
        }
        else if (v.getId() == R.id.go_to_edit_button) {
            startActivity(new Intent(this, EditDataActivity.class));
        }
        else if (v.getId() == R.id.go_to_change_password_button) {
            startActivity(new Intent(this, EditPasswordActivity.class));
        }
    }

    private void signOut() {
        AuthService.signOut();
        finishAffinity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}