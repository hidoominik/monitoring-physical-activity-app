package com.example.phisicalactivitymonitoringapp.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;

import com.example.phisicalactivitymonitoringapp.DeleteAccountActivity;
import com.example.phisicalactivitymonitoringapp.EditDataActivity;
import com.example.phisicalactivitymonitoringapp.EditPasswordActivity;
import com.example.phisicalactivitymonitoringapp.R;
import com.example.phisicalactivitymonitoringapp.databinding.ActivityUserProfileBinding;
import com.example.phisicalactivitymonitoringapp.shared.navigation.DrawerBaseActivity;
import com.example.phisicalactivitymonitoringapp.user.model.User;
import com.example.phisicalactivitymonitoringapp.workouts.ShowStatsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.Synchronized;

public class UserProfileActivity extends DrawerBaseActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String anotherUserUsername;
    private TextView usernameTextView;
    private TextView weight;
    private TextView height;
    private Button deleteButton;
    private Button editButton;
    private Button editPasswordButton;
    private Button subscribeButtton;
    private Button unsubscribeButton;
    private Button showStatsButton;

    private Button editAvatarButton;
    private DatabaseReference mDatabase;

    private StorageReference mStorage;
    private ImageFilterView avatarView;
    private File avatarLocalFile;
    private Uri avatarLocalUri;

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;

    String[] cameraPermission;
    String[] storagePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityUserProfileBinding.inflate(getLayoutInflater()).getRoot());

        findViews();

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference("Avatars");
        avatarLocalUri = Uri.fromFile(new File("drawable/user_placeholder.png"));


        currentUser = mAuth.getCurrentUser();
        anotherUserUsername = getIntent().getSerializableExtra("username") != null
                ? getIntent().getSerializableExtra("username").toString()
                : null;

        showStatsButton = findViewById(R.id.userStatsButton);
        showStatsButton.setOnClickListener(this);

        loadUserData();

        setView();

        try {
            loadProfileImage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.go_to_delete_button) {
            startActivity(new Intent(this, DeleteAccountActivity.class));
        } else if (v.getId() == R.id.go_to_edit_button) {
            startActivity(new Intent(this, EditDataActivity.class));
        } else if (v.getId() == R.id.go_to_change_password_button) {
            startActivity(new Intent(this, EditPasswordActivity.class));
        } else if (v.getId() == R.id.userProfileSubscribeButton) {
            subscribeUser();
        } else if (v.getId() == R.id.userProfileUnsubscribeButton) {
            unsubscribeUser();
        } else if (v.getId() == R.id.userStatsButton) {
            String usernameForShowUserDetails = anotherUserUsername != null ?
                    anotherUserUsername : currentUser.getDisplayName();

            Intent intent = new Intent(this, ShowStatsActivity.class);
            intent.putExtra("username", usernameForShowUserDetails);
            startActivity(intent);
        } else if (v.getId() == R.id.profileImage) {
            showImagePicDialog();
        }
    }

    private void subscribeUser() {

        if (anotherUserUsername != null && !Objects.requireNonNull(currentUser.getDisplayName()).isEmpty()) {
            Map<String, Object> updateWatchedUsersMap = new HashMap<>();
            updateWatchedUsersMap.put(anotherUserUsername, true);
            Map<String, Object> updateWatchingUsersMap = new HashMap<>();
            updateWatchingUsersMap.put(currentUser.getDisplayName(), true);

            mDatabase.child(currentUser.getDisplayName()).child("watchedUsers").updateChildren(updateWatchedUsersMap);
            mDatabase.child(anotherUserUsername).child("watchingUsers").updateChildren(updateWatchingUsersMap);

            subscribeButtton.setVisibility(View.GONE);
            unsubscribeButton.setVisibility(View.VISIBLE);
            unsubscribeButton.setOnClickListener(this);
            Toast.makeText(this, "User has been subscribed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error during subscribing", Toast.LENGTH_SHORT).show();
        }
    }

    private void unsubscribeUser() {

        if (anotherUserUsername != null && !Objects.requireNonNull(currentUser.getDisplayName()).isEmpty()) {

            mDatabase.child(currentUser.getDisplayName()).child("watchedUsers").child(anotherUserUsername).removeValue();
            mDatabase.child(anotherUserUsername).child("watchingUsers").child(currentUser.getDisplayName()).removeValue();

            unsubscribeButton.setVisibility(View.GONE);
            subscribeButtton.setVisibility(View.VISIBLE);
            subscribeButtton.setOnClickListener(this);
            Toast.makeText(this, "User has been unsubscribed", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Error during unsubscribing", Toast.LENGTH_SHORT).show();
        }
    }

    private void setView() {
        if (anotherUserUsername != null) {
            setUserViewDependsOfSubscription();
        } else {
            setCurrentUserProfileView();
        }
    }

    @Synchronized
    private void setUserViewDependsOfSubscription() {

        mDatabase.child(Objects.requireNonNull(currentUser.getDisplayName())).child("watchedUsers").child(anotherUserUsername).get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                unsubscribeButton.setVisibility(View.VISIBLE);
                unsubscribeButton.setOnClickListener(this);
            } else {
                subscribeButtton.setVisibility(View.VISIBLE);
                subscribeButtton.setOnClickListener(this);
            }

        });
    }

    private void loadProfileImage() throws IOException {

        mStorage = anotherUserUsername != null
                ? mStorage.child(anotherUserUsername + ".png")
                : mStorage.child(currentUser.getDisplayName() + ".png");

        avatarLocalFile = File.createTempFile("avatars", "png");
        mStorage.getFile(avatarLocalFile)
                .addOnSuccessListener(taskSnapshot -> {
                    avatarLocalUri = Uri.fromFile(avatarLocalFile);
                    avatarView.setImageURI(avatarLocalUri);
                }).addOnCompleteListener(exception -> {
                    avatarLocalUri = Uri.fromFile(avatarLocalFile);
                });
    }

    private void setCurrentUserProfileView() {
        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
        editPasswordButton.setVisibility(View.VISIBLE);
        editAvatarButton.setVisibility(View.VISIBLE);
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        editPasswordButton.setOnClickListener(this);
        avatarView.setOnClickListener(this);
    }

    private void hideButtons() {
        editButton.setVisibility(View.GONE);
        editPasswordButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        subscribeButtton.setVisibility(View.GONE);
        unsubscribeButton.setVisibility(View.GONE);
    }


    private void findViews() {
        usernameTextView = findViewById(R.id.username);
        weight = findViewById(R.id.weight_value);
        height = findViewById(R.id.height_value);
        deleteButton = findViewById(R.id.go_to_delete_button);
        editButton = findViewById(R.id.go_to_edit_button);
        editPasswordButton = findViewById(R.id.go_to_change_password_button);
        subscribeButtton = findViewById(R.id.userProfileSubscribeButton);
        unsubscribeButton = findViewById(R.id.userProfileUnsubscribeButton);
        avatarView = findViewById(R.id.profileImage);
        editAvatarButton = findViewById(R.id.editAvatarButton);
    }

    private void loadUserData() {
        if (currentUser != null) {

            String usernameForShowUserDetails = anotherUserUsername != null ? anotherUserUsername : currentUser.getDisplayName();

            Query emailQuery = mDatabase.orderByChild("username").equalTo(usernameForShowUserDetails);
            emailQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {

                            usernameTextView.setText(user.getUsername());

                            if (user.getWeight() == null)
                                weight.setText(R.string.no_data_info);
                            else
                                weight.setText(user.getWeight());

                            if (user.getWeight() == null)
                                height.setText(R.string.no_data_info);
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
    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromGallery();
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private Boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    private void pickFromGallery() {
        Intent pickImageIntent = new Intent();
        pickImageIntent.setType("image/*");
        pickImageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickImageIntent, IMAGEPICK_GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && requestCode == IMAGEPICK_GALLERY_REQUEST
                && data != null
                && data.getData() != null) {

            Uri uri = data.getData();

            UCrop.of(uri, avatarLocalUri)
                    .withAspectRatio(1, 1)
                    .start(UserProfileActivity.this);
        } else if (resultCode == RESULT_OK
                && requestCode == IMAGE_PICKCAMERA_REQUEST
                && data != null
                && data.getData() != null) {

            //TODO: Not implemented yet
            Toast.makeText(UserProfileActivity.this, "Not implemented yet", Toast.LENGTH_LONG).show();

        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri croppedImageUri = UCrop.getOutput(data);

            if (croppedImageUri == null) {
                Toast.makeText(UserProfileActivity.this, "Cannot upload empty file", Toast.LENGTH_LONG).show();
            } else {
                mStorage.putFile(croppedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            Intent intent = new Intent(this, UserProfileActivity.class);
                            finishAffinity();
                            startActivity(intent);
                        });
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }
}