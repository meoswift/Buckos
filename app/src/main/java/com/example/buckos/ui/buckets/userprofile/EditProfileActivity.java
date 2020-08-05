package com.example.buckos.ui.buckets.userprofile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.User;
import com.example.buckos.ui.buckets.PhotoHandler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

// Activity that allows user to edit their username, display name, and bio. Changes will be
// saved when they exits out of activity.
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    public static final String FILENAME = "photo.png";
    public static final String AUTHORITY = "com.codepath.fileprovider.buckos";

    private EditText mNameEditText;
    private EditText mUsernameEditText;
    private EditText mBioEditText;
    private ImageView mProfilePicImageView;
    private File photoFile;

    private ParseFile profilePicFile;
    private User mUser;
    private PhotoHandler photoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Find views
        mNameEditText = findViewById(R.id.nameEt);
        mUsernameEditText = findViewById(R.id.usernameEt);
        mBioEditText = findViewById(R.id.bioEt);

        ImageView backButton = findViewById(R.id.backButton);
        ImageView saveButton = findViewById(R.id.saveEditBtn);
        TextView changeProfileTextView = findViewById(R.id.changeProfilePicTv);
        mProfilePicImageView = findViewById(R.id.authorProfilePic);

        // Populate views with user info
        mUser = (User) ParseUser.getCurrentUser();
        populateUserInfo();

        photoHandler = new PhotoHandler(getApplicationContext(), this);

        backButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        changeProfileTextView.setOnClickListener(this);
        mProfilePicImageView.setOnClickListener(this);

    }

    private void populateUserInfo() {
        mNameEditText.setText(mUser.getName());
        mUsernameEditText.setText(mUser.getUsername());
        mBioEditText.setText(mUser.getBio());
        setProfilePic();
    }

    // Set profile pic with either file from database or default image
    private void setProfilePic() {
        ParseFile image = (ParseFile) mUser.get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(this).load(image.getUrl())
                    .circleCrop().into(mProfilePicImageView);
        else
            Glide.with(this).load(R.drawable.no_profile_pic)
                    .circleCrop().into(mProfilePicImageView);
    }

    // Save all changes and update user profile
    public void saveChangesOnSavePressed() {
        final User user = (User) ParseUser.getCurrentUser();
        user.setName(mNameEditText.getText().toString());
        user.setUsername(mUsernameEditText.getText().toString());
        user.setBio(mBioEditText.getText().toString());
        if (profilePicFile != null)
            user.setProfilePic(profilePicFile);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // Build a dialog that shows two option for user to pick a photo from
    private void choosePhotoOption() {
        String[] options = {"Choose image", "Take a photo"};
        new MaterialAlertDialogBuilder(EditProfileActivity.this)
                .setTitle("Add profile image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            choosePhotoFromGallery();
                        else
                            takePhotoFromCamera();
                    }
                })
                .show();
    }

    // Starts an intent to open the camera and save image
    private void takePhotoFromCamera() {
        // Create a File reference for future access
        photoFile = photoHandler.getPhotoFileUri(FILENAME);
        Uri fileProvider = FileProvider.getUriForFile(this, AUTHORITY, photoFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // makes sure intent can be resolved
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    private void choosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // Set image taken from camera to profile pic
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Glide.with(this).load(takenImage).circleCrop().into(mProfilePicImageView);
                profilePicFile = new ParseFile(photoFile);
            } else {
                Toast.makeText(this, R.string.media_fail, Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();

                // Create a ParseFile from URI and add uploaded photo to item
                try {
                    InputStream inputStream = getContentResolver().openInputStream(photoUri);
                    // need to convert uri into a bytes array
                    byte[] inputData = photoHandler.getBytes(inputStream);
                    Glide.with(this).load(photoUri).circleCrop().into(mProfilePicImageView);
                    profilePicFile = new ParseFile(FILENAME, inputData);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, R.string.media_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Handle back button clicked - changes not saved
            case R.id.backButton:
                EditProfileActivity.super.onBackPressed();
                break;
            // Handle save button clicked - update changes
            case R.id.saveEditBtn:
                saveChangesOnSavePressed();
                break;
            // Opens camera or gallery - save new photo as profile pic
            case R.id.changeProfilePicTv:
            case R.id.authorProfilePic:
                choosePhotoOption();
                break;
        }
    }
}