package com.example.buckos.main.profile.bucketlists.items.content;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.main.profile.bucketlists.items.Item;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// This class displays the details of a bucket list item - Title and Note
// User can also edit their note in this activity
public class ItemDetailsActivity extends AppCompatActivity {

    public static final String DELETE_ITEM = "deleteItem";
    public static final String EDIT_ITEM = "editItem";
    public final String APP_TAG = "Buckos";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private EditText mItemTitleEt;
    private EditText mItemNoteEt;
    private ImageView mBackButtonIv;
    private TextView mShareTv;
    private ImageView mDeleteButton;
    private TextView mListStatusTv;
    private FloatingActionButton mAddPhotoButton;
    private NestedScrollView mNestedScrollView;
    private RecyclerView mPhotosRv;

    private File photoFile;
    private Item item;
    private int itemPosition;
    private List<Photo> mPhotosInItem;
    private PhotosAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get the item object that user clicked on and the position of that item in RV
        Intent intent = getIntent();
        item = Parcels.unwrap(intent.getParcelableExtra("item"));
        itemPosition = intent.getExtras().getInt("position");

        // Find views
        mItemTitleEt = findViewById(R.id.itemTitleEt);
        mItemNoteEt = findViewById(R.id.itemNoteEt);
        mBackButtonIv = findViewById(R.id.backButton);
        mShareTv = findViewById(R.id.shareTv);
        mNestedScrollView = findViewById(R.id.nestedScroll);
        mListStatusTv = findViewById(R.id.listStatus);
        mAddPhotoButton = findViewById(R.id.addPhotoBtn);
        mDeleteButton = findViewById(R.id.deleteButton);
        mPhotosRv = findViewById(R.id.photosRv);

        // Populate title and note of an item
        populateItemDetails();

        // Set up adapter for list of photos attached to an item
        mPhotosInItem = new ArrayList<>();
        mAdapter = new PhotosAdapter(mPhotosInItem, this);
        mPhotosRv.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mPhotosRv.setLayoutManager(layoutManager);

        // When user press back, save all changes and update to database
        mBackButtonIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });
        // When user click Trash icon, delete the item from list
        handleDeleteItemClicked();
        // When user scroll through the item, they can read or edit the item on touch
        handleScrollViewClicked();
        // When user click add photo, they can choose photo from gallery or camera
        handleAddPhotoButtonClicked();
    }

    // When user navigate via hardware back press, also save changes and update
    @Override
    public void onBackPressed() {
        saveChanges();
    }

    private void handleAddPhotoButtonClicked() {
        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhotoOption();
            }
        });
    }

    // On each menu option, calls the right function to perform the task
    public void handleDeleteItemClicked() {
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
    }


    // When scroll view is touched, determine which one is a tap, and which is a scroll
    @SuppressLint("ClickableViewAccessibility")
    public void handleScrollViewClicked() {
        mNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            private long startClickTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // starts timing when user click down on screen
                    startClickTime = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    // keyboard manager
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    // Touch was a simple tap - focus on edit text and show keyboard
                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                        mItemNoteEt.requestFocus();
                        imm.showSoftInput(mItemNoteEt, 0);

                    // Touch was scrolling - clear focus and hide keyboard
                    } else {
                        mItemTitleEt.clearFocus();
                        mItemNoteEt.clearFocus();
                        imm.hideSoftInputFromWindow(mNestedScrollView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }

    // Populate views
    private void populateItemDetails() {
        mItemTitleEt.setText(item.getName()); // set item title
        if (item.getDescription() != null)   // set item description if available
            mItemNoteEt.setText(item.getDescription());
        // If item clicked on is already completed, do not show option to Share
        if (!item.getCompleted()) {
            mShareTv.setText(null);
            mListStatusTv.setText("In progress");
        }
    }

    // Set item's properties with changes and save in background
    private void saveChanges() {

        item.setName(mItemTitleEt.getText().toString());
        item.setDescription(mItemNoteEt.getText().toString());

        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                intent.putExtra("item", Parcels.wrap(item));
                intent.putExtra("position", itemPosition);
                intent.putExtra("action", EDIT_ITEM);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // Remove item from database and update RecyclerView
    private void deleteItem() {
        item.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                intent.putExtra("position", itemPosition);
                intent.putExtra("action", DELETE_ITEM);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // Build a dialog that shows two option for user to pick a photo from
    private void choosePhotoOption() {
        String [] options = {"Choose image", "Take a photo"};
        new MaterialAlertDialogBuilder(ItemDetailsActivity.this)
                .setTitle("Add image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                            Log.d("debug", "choose image");
                        else
                            takePhotoFromCamera();
                    }
                })
                .show();
    }

    // When user click on Capture button, starts an intent to open the camera
    public void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        String photoFileName = "photo.png";
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(this,
                "com.codepath.fileprovider.buckos", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If call startActivityForResult() using an intent that no app can handle, app will crash.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "Failed to create!");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                addNewPhoto(item);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addNewPhoto(Item item) {
        final Photo photo = new Photo();
        photo.setItem(item);
        photo.setPhotoFile(new ParseFile(photoFile));

        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mPhotosInItem.add(photo);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


}