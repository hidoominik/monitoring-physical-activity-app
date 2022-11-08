package com.example.phisicalactivitymonitoringapp.authorization;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.google.firebase.auth.FirebaseAuth;

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView banner, registerUser, backToLogin;
    private EditText editTextEmail, editTextUsername, editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        mAuth = FirebaseAuth.getInstance();

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        backToLogin = (TextView) findViewById(R.id.backToLogin);
        backToLogin.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextUsername = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
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

        if(email.isEmpty()){
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Provide valid email.");
            editTextEmail.requestFocus();
            return;
        }
        if(username.isEmpty()){
            editTextUsername.setError("Username is required");
            editTextUsername.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6){ //Firebase requirements, password.length >= 6
            editTextPassword.setError("Password is length should minimum 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                new User();
                Toast.makeText(UserRegistrationActivity.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(UserRegistrationActivity.this, MainActivity.class));

            }else{
                Toast.makeText(UserRegistrationActivity.this, "Something went wrong...", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}