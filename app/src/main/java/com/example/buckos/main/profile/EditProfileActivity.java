package com.example.buckos.main.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.buckos.R;
import com.example.buckos.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class EditProfileActivity extends AppCompatActivity {

    private EditText mNameEt;
    private EditText mUsernameEt;
    private EditText mBioEt;
    private ImageView mBackButton;
    private ImageView mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Find views
        mNameEt = findViewById(R.id.nameEt);
        mUsernameEt = findViewById(R.id.usernameEt);
        mBioEt = findViewById(R.id.bioEt);
        mBackButton = findViewById(R.id.backButton);
        mSaveButton = findViewById(R.id.saveEditBtn);

        // Populate views with user info
        populateUserInfo();

        // Handle back button clicked - changes not saved
        dismissChangesOnBackPressed();
        // Handle save button clicked - update changes
        saveChangesOnSavePressed();
    }

    private void populateUserInfo() {
        User user = (User) ParseUser.getCurrentUser();
        mNameEt.setText(user.getName());
        mUsernameEt.setText(user.getUsername());
        mBioEt.setText(user.getBio());
    }

    public void dismissChangesOnBackPressed() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileActivity.super.onBackPressed();
            }
        });
    }

    public void saveChangesOnSavePressed() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = (User) ParseUser.getCurrentUser();
                user.setName(mNameEt.getText().toString());
                user.setUsername(mUsernameEt.getText().toString());
                user.setBio(mBioEt.getText().toString());

                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
    }

}