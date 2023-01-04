package com.example.phisicalactivitymonitoringapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phisicalactivitymonitoringapp.DeleteAccountActivity;
import com.example.phisicalactivitymonitoringapp.EditDataActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.model.User;
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

    private DatabaseReference mDatabase;

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
        editButton.setOnClickListener(this);

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
                            weight.setText(user.getWeight());
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
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_to_delete_button) {
            startActivity(new Intent(this, DeleteAccountActivity.class));
        }
        else if (v.getId() == R.id.go_to_edit_button) {
            startActivity(new Intent(this, EditDataActivity.class));
        }
    }
}