package com.example.phisicalactivitymonitoringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phisicalactivitymonitoringapp.user.UserProfileActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView newPasswordTextView;
    private TextView oldPasswordTextView;
    private EditText newPasswordEditText;
    private EditText oldPasswordEditText;
    private Button saveButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        newPasswordTextView = findViewById(R.id.edit_password_label);
        oldPasswordTextView = findViewById(R.id.old_password_label);
        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        oldPasswordEditText = findViewById(R.id.old_password_edit_text);
        saveButton = findViewById(R.id.save_new_password_button);

        saveButton.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save_new_password_button) {
            changePassword();
        }
    }

    public void changePassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String oldPassword = oldPasswordEditText.getText().toString().trim();

        if (newPassword.isEmpty()) {
            newPasswordTextView.setError("Password is required");
            newPasswordTextView.requestFocus();
            return;
        }

        final String PASSWORD_PATTERN = "(^(?=.*[A-Za-z0-9]$)[A-Za-z][A-Za-z\\d.-]{4,45}$)";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(newPassword);

        if (!matcher.matches()) {
            newPasswordTextView.setError("Wrong password - create password 4-45 with letters, numbers or - char");
            newPasswordTextView.requestFocus();
            return;
        }

        if (oldPassword.isEmpty()) {
            oldPasswordTextView.setError("Password is required");
            oldPasswordTextView.requestFocus();
            return;
        }

        matcher = pattern.matcher(oldPassword);

        if (!matcher.matches()) {
            oldPasswordTextView.setError("Wrong password - create password 4-45 with letters, numbers or - char");
            oldPasswordTextView.requestFocus();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(
                    Objects.requireNonNull(currentUser.getEmail()), oldPassword);

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(EditPasswordActivity.this,
                                            "Password was successfully changed!",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EditPasswordActivity.this, UserProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.d("ERROR", "Error password not updated");
                                }
                            });
                        } else {
                            Toast.makeText(EditPasswordActivity.this,
                                    "Wrong password given!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}