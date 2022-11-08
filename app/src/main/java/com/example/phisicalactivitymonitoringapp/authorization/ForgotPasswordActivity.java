package com.example.phisicalactivitymonitoringapp.authorization;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phisicalactivitymonitoringapp.MainActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText emailEditText;
    private Button resetPasswordButton, goToLogin;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = (EditText) findViewById(R.id.email);
        resetPasswordButton = (Button) findViewById(R.id.resetPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        goToLogin = (Button) findViewById(R.id.goToLogin);
        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(view -> resetPassword());
        goToLogin.setOnClickListener(view -> startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class)));
    }

    private void resetPassword(){
        String email = emailEditText.getText().toString().trim();
        if(email.isEmpty()){
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide valid email!");
            emailEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(ForgotPasswordActivity.this,"Check your email to reset your password.", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

            }else{
                Toast.makeText(ForgotPasswordActivity.this,"Something went wrong...", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}