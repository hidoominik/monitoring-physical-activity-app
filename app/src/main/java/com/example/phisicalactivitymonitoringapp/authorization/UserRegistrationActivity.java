package com.example.phisicalactivitymonitoringapp.authorization;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView banner, registerUser, backToLogin;
    private EditText editTextEmail, editTextUsername, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        banner = findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        backToLogin = findViewById(R.id.backToLogin);
        backToLogin.setOnClickListener(this);

        editTextEmail = findViewById(R.id.email);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.banner:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.registerUser:
                registerUser();
                break;
            case R.id.backToLogin:
                startActivity(new Intent(this, UserLoginActivity.class));
        }

    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Provide valid email.");
            editTextEmail.requestFocus();
            return;
        }
        if (username.isEmpty()) {
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        Pattern pattern;
        Matcher matcher;

        final String USERNAME_PATTERN = "(^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\\d.-]{4,45}$)";

        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);

        if (!matcher.matches()) {
            editTextUsername.setError("Wrong username - create username 4-45 with letters, numbers or - char");
            editTextUsername.requestFocus();
            return;
        }

        final String PASSWORD_PATTERN = "(^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\\d.-]{4,45}$)";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            editTextPassword.setError("Wrong password - create password 4-45 with letters, numbers or - char");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();

            if (!isNewUser) {
                editTextEmail.setError("Choose different email");
                editTextEmail.requestFocus();
            }
        });

        progressBar.setVisibility(View.VISIBLE);

        mDatabase.child(username).get().addOnCompleteListener(userSearchTask -> {
            if (userSearchTask.getResult().exists()) {
                finishProgressBarWithToast("User already exists in database");
                editTextUsername.setError("User already exists in database");
                editTextUsername.requestFocus();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authTaskSucceeded -> mDatabase.child(username).setValue(new User(username, email))
                                .addOnSuccessListener(databaseTaskSucceeded -> {
                                    finishProgressBarWithToast("User has been registered successfully");
                                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).sendEmailVerification();
                                    startActivity(new Intent(UserRegistrationActivity.this, UserLoginActivity.class));
                                }).addOnFailureListener(databaseTaskFailed -> {
//                                TODO: Dodać usuwanie usera z FirebaseAuth przy niepowodzeniu utworzenia go w RealtimeDatabase
                                    finishProgressBarWithToast(databaseTaskFailed.getLocalizedMessage());
                                }))
                        .addOnFailureListener(authTaskFailed -> finishProgressBarWithToast(authTaskFailed.getLocalizedMessage()));
            }
        });
    }

    private void finishProgressBarWithToast(String toastMessage) {
        Toast.makeText(UserRegistrationActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

}