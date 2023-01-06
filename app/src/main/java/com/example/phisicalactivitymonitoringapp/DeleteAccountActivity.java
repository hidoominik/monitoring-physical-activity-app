package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phisicalactivitymonitoringapp.authorization.UserLoginActivity;
import com.example.phisicalactivitymonitoringapp.authorization.services.AuthService;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DeleteAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser currentUser;
    private AuthCredential authCredential;

    private Button deleteButton;
    private TextView passwordLabel;
    private EditText password;

    private FirebaseDatabase database;
    private DatabaseReference users;
    private DatabaseReference workouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        deleteButton = findViewById(R.id.delete_account_button);
        passwordLabel = findViewById(R.id.delete_account_text_view);
        password = findViewById(R.id.password_input);

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        workouts = database.getReference("Workouts");

        deleteButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.delete_account_button) {
            String passwordValue = password.getText().toString().trim();

            if (passwordValue.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {
                authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), passwordValue);

                System.out.println(currentUser.getEmail());
                System.out.println(passwordValue);

                currentUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser.delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                deleteWorkoutsList();
                                Log.d("TAG", "User account deleted!");
                                Toast.makeText(DeleteAccountActivity.this, "Deleted User Successfully", Toast.LENGTH_LONG).show();
                                signOut();
                            }
                        });
                    } else {
                        password.setError("Password isn't correct");
                        password.requestFocus();
                    }
                });
            }
        }
    }

    public void deleteWorkoutsList() {
        if (currentUser != null) {
            Query emailQuery = users.orderByChild("email").equalTo(currentUser.getEmail());
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            Query workoutsQuery = workouts.orderByChild("username").equalTo(user.getUsername());
                            workoutsQuery.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        ds.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("onCancelled", String.valueOf(error.toException()));
                                }
                            });
                        }
                        child.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("onCancelled", error.getMessage());
                }
            });
        }
    }

    private void signOut() {
        AuthService.signOut();
        finishAffinity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent intent = new Intent(DeleteAccountActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}