package com.example.buckos.main_screen.travel_explore.save_place_to_bucket_list;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.main_screen.travel_explore.TravelFragment;
import com.example.buckos.main_screen.travel_explore.list_of_places_in_city.Place;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.BucketList;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.bucket_list_items.Item;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SaveToListActivity extends AppCompatActivity {

    private ImageView mBackButton;
    private ImageView mSaveButton;
    private RecyclerView mTravelListsRv;
    private ProgressBar mProgressBar;

    private List<BucketList> mTravelLists;
    private TravelListsAdapter mAdapter;
    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_to_list);

        Intent intent = getIntent();
        place = Parcels.unwrap(intent.getParcelableExtra("place"));

        // Find views
        mBackButton = findViewById(R.id.backButton);
        mSaveButton = findViewById(R.id.saveButton);
        mTravelListsRv = findViewById(R.id.travelListsRv);
        mProgressBar = findViewById(R.id.progressBar);

        // Set up adapter for travel lists
        mTravelLists = new ArrayList<>();
        mAdapter = new TravelListsAdapter(mTravelLists, this);
        mTravelListsRv.setAdapter(mAdapter);
        mTravelListsRv.setLayoutManager(new LinearLayoutManager(this));

        queryLists();
        saveToSelectedLists();
    }

    // Display the bucket lists that user has created
    private void queryLists() {
        // Specify which class to query
        ParseQuery<BucketList> query = ParseQuery.getQuery(BucketList.class);
        // get only lists that belong to current users
        query.whereEqualTo(BucketList.KEY_AUTHOR, ParseUser.getCurrentUser());
        // order the posts from newest to oldest
        query.orderByDescending(BucketList.KEY_CREATED_AT);
        // start an asynchronous call for lists
        query.findInBackground(new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e != null) {
                    Log.d("SaveToListActivity", "Issue with querying posts" + e);
                    return;
                }

                mTravelLists.addAll(objects);
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    // When user click on save button, save the Place to all selected travel lists
    private void saveToSelectedLists() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<BucketList> selectedLists = mAdapter.getSelectedLists();
                Toast.makeText(getApplicationContext(), "Saved to lists", Toast.LENGTH_LONG).show();
                // Go through selected travel lists and add place to each list
                for (int i = 0; i < selectedLists.size(); i++) {
                    addItemToList(selectedLists.get(i));
                }
            }
        });
    }

    // Create a new instance of Item for each list and add that item to the list
    private void addItemToList(BucketList list) {
        final Item item = new Item();
        // Set core properties
        item.setName(place.getName());
        item.setCompleted(false);
        item.setAuthor(ParseUser.getCurrentUser());
        item.setDescription(place.getAddressName());
        item.setList(list);
        // Save to database
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
    }

}