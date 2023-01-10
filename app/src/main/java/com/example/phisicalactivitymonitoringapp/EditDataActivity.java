package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class EditDataActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText weightInput;
    private EditText heightInput;
    private Button saveChangedDataButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        weightInput = findViewById(R.id.weight_input);
        heightInput = findViewById(R.id.height_input);
        saveChangedDataButton = findViewById(R.id.save_changed_data_button);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        saveChangedDataButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save_changed_data_button) {
            saveChangedData();
        }
    }

    private void saveChangedData() {
        String weight = weightInput.getText().toString().trim();
        String height = heightInput.getText().toString().trim();

        if (weight.isEmpty()) {
            weightInput.setError("Weight is required");
            weightInput.requestFocus();
            return;
        }
        if (height.isEmpty()) {
            heightInput.setError("Height is required");
            heightInput.requestFocus();
            return;
        }

        if (currentUser != null) {
            Query emailQuery = mDatabase.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            mDatabase.child(user.getUsername()).child("weight").setValue(weight);
                            mDatabase.child(user.getUsername()).child("height").setValue(height);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("TAG", error.getMessage());
                }
            });
        }

        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}