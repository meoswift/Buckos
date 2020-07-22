package com.example.buckos.app.buckets.userprofile;

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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

// Activity that allows user to edit their username, display name, and bio. Changes will be
// saved when they exits out of activity.
public class EditProfileActivity extends AppCompatActivity {

    public final String APP_TAG = "Buckos";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;

    private EditText mNameEditText;
    private EditText mUsernameEditText;
    private EditText mBioEditText;
    private ImageView mBackButton;
    private ImageView mSaveButton;
    private TextView mChangeProfileTextView;
    private ImageView mProfilePicImageView;
    private File photoFile;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Find views
        mNameEditText = findViewById(R.id.nameEt);
        mUsernameEditText = findViewById(R.id.usernameEt);
        mBioEditText = findViewById(R.id.bioEt);
        mBackButton = findViewById(R.id.backButton);
        mSaveButton = findViewById(R.id.saveEditBtn);
        mChangeProfileTextView = findViewById(R.id.changeProfilePicTv);
        mProfilePicImageView = findViewById(R.id.profilePicIv);

        // Populate views with user info
        mUser = (User) ParseUser.getCurrentUser();
        populateUserInfo();

        // Handle back button clicked - changes not saved
        dismissChangesOnBackPressed();
        // Handle save button clicked - update changes
        saveChangesOnSavePressed();
        // Opens camera - save new photo as profile pic
        handleChangeProfilePicClicked();
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
            Glide.with(this).load(image.getUrl()).circleCrop().into(mProfilePicImageView);
        else
            Glide.with(this).load(R.drawable.ic_launcher_background)
                    .circleCrop().into(mProfilePicImageView);
    }


    // Close edit profile screen
    public void dismissChangesOnBackPressed() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileActivity.super.onBackPressed();
            }
        });
    }

    // Save all changes and update user profile
    public void saveChangesOnSavePressed() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = (User) ParseUser.getCurrentUser();
                user.setName(mNameEditText.getText().toString());
                user.setUsername(mUsernameEditText.getText().toString());
                user.setBio(mBioEditText.getText().toString());
                if (photoFile != null)
                    user.setProfilePic(photoFile);

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

    private void handleChangeProfilePicClicked() {
        mChangeProfileTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoOption();
            }
        });

        mProfilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoOption();
            }
        });
    }

    // Build a dialog that shows two option for user to pick a photo from
    private void choosePhotoOption() {
        String[] options = {"Choose image", "Take a photo"};
        new MaterialAlertDialogBuilder(EditProfileActivity.this)
                .setTitle("Add image")
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
        String photoFileName = "photo.png";
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(this,
                "com.codepath.fileprovider.buckos", photoFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // makes sure intent can be resolved
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "Failed to create!");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }


    private void choosePhotoFromGallery() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Set image taken from camera to profile pic
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Glide.with(this).load(takenImage).circleCrop().into(mProfilePicImageView);
        } else {
            Toast.makeText(this, "Picture wasn't taken.", Toast.LENGTH_SHORT).show();
        }
    }
}