package com.example.buckos.ui.buckets.items.itemdetails;

import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.ui.buckets.items.ItemsAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// This class displays the details of a bucket list item - Title and Note
// User can also edit their note in this activity
public class ItemDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DELETE_ITEM = "deleteItem";
    public static final String EDIT_ITEM = "editItem";
    public static final String POST_ITEM = "postItem";
    public final String APP_TAG = "Buckos";

    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;

    private EditText mItemTitleEditText;
    private EditText mItemNoteEditText;
    private ImageView mBackButtonImageView;
    private TextView mPostTextView;
    private ImageView mDeleteButton;
    private TextView mListStatusTextView;
    private ImageView mAddPhotoButton;
    private RecyclerView mPhotosRv;

    private File photoFile;
    private Item item;
    private int itemPosition;
    private List<Photo> mPhotosInItem;
    private PhotosAdapter mPhotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get the item object that user clicked on and the position of that item in RV
        Intent intent = getIntent();
        item = Parcels.unwrap(intent.getParcelableExtra("item"));
        itemPosition = intent.getExtras().getInt("position");

        // Find views
        mItemTitleEditText = findViewById(R.id.itemTitleEt);
        mItemNoteEditText = findViewById(R.id.itemNoteEt);
        mBackButtonImageView = findViewById(R.id.backButton);
        mPostTextView = findViewById(R.id.postTv);
        mListStatusTextView = findViewById(R.id.listStatus);
        mAddPhotoButton = findViewById(R.id.addPhotoBtn);
        mDeleteButton = findViewById(R.id.deleteButton);
        mPhotosRv = findViewById(R.id.photosRv);

        setUpAdapterForPhotos();

        // Executes appropriate functions depends on which button clicked
        mBackButtonImageView.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mAddPhotoButton.setOnClickListener(this);
        mPostTextView.setOnClickListener(this);

        // Populate title, note, and photos attached in an item
        populateItemDetails();
    }

    // Set up adapter for list of photos attached to an item
    private void setUpAdapterForPhotos() {
        mPhotosInItem = new ArrayList<>();
        mPhotosAdapter = new PhotosAdapter(mPhotosInItem, this);
        mPhotosRv.setAdapter(mPhotosAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mPhotosRv.setLayoutManager(layoutManager);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.backButton:
                saveEditItemChanges();
                break;
            case R.id.deleteButton:
                deleteItem();
                break;
            case R.id.addPhotoBtn:
                choosePhotoOption();
                break;
            case R.id.postTv:
                postNewStory();
                break;
        }
    }

    // Populate views of an item: name, description, checkbox
    private void populateItemDetails() {
        mItemTitleEditText.setText(item.getName());
        if (item.getDescription() != null)
            mItemNoteEditText.setText(item.getDescription());
        // If item clicked on is already completed, do not show option to Share
        if (!item.getCompleted()) {
            mPostTextView.setText(null);
            mListStatusTextView.setText("In progress");
        }
        mPhotosAdapter.displayPhotosInCurrentItem(item);
    }

    // Set item's properties with changes and save in background
    private void saveEditItemChanges() {
        item.setName(mItemTitleEditText.getText().toString());
        item.setDescription(mItemNoteEditText.getText().toString());

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
    public void deleteItem() {
        item.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                mPhotosAdapter.deleteAllPhotosInItem(item);
                deleteStoriesOfItem(item);
                Intent intent = new Intent();
                intent.putExtra("position", itemPosition);
                intent.putExtra("action", ItemDetailsActivity.DELETE_ITEM);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // Remove all stories related to this item
    public void deleteStoriesOfItem(Item item) {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        query.whereEqualTo(Story.KEY_ITEM, item);
        query.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> stories, ParseException e) {
                for (Story story : stories) {
                    story.deleteInBackground();
                }
            }
        });
    }

    // Post story of a completed item to Home feed
    private void postNewStory() {
        // create new story instance
        Story story = new Story();

        // save in database any changes before posting
        saveEditItemChanges();

        // set core properties of a story
        story.setAuthor(ParseUser.getCurrentUser());
        story.setTitle(item.getName());
        story.setDescription(item.getDescription());
        story.setItem(item);
        story.setList(item.getList());

        story.saveInBackground();

        // navigates user to Home Feed to see their new post
        Intent intent = new Intent();
        intent.putExtra("action", ItemDetailsActivity.POST_ITEM);
        setResult(RESULT_OK, intent);
        finish();
    }


    // Build a dialog that shows two option for user to pick a photo from
    private void choosePhotoOption() {
        String[] options = {"Choose image", "Take a photo"};
        new MaterialAlertDialogBuilder(ItemDetailsActivity.this)
                .setTitle("Add an image")
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

    public void takePhotoFromCamera() {
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

    // When user click Choose from Gallery, starts intent for gallery selection
    public void choosePhotoFromGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
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
        // Use image taken from camera
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            mPhotosAdapter.addNewPhoto(item, photoFile);
        } else {
            Toast.makeText(this, "Picture wasn't taken.", Toast.LENGTH_SHORT).show();
        }

        // Use image picked from gallery
        if (requestCode == PICK_PHOTO_CODE && resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
//            mPhotosAdapter.addNewPhoto(item, photoFile);
        } else {
            Toast.makeText(this, "Fail to choose media.", Toast.LENGTH_SHORT).show();
        }
    }


    // When user navigate via hardware back press, also save changes and update
    @Override
    public void onBackPressed() {
        saveEditItemChanges();
    }

}