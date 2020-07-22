package com.example.buckos.ui.travel.placebookmark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.example.buckos.models.Place;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// Activity that allows user to select multiple bucket lists that they want to add a Place to
public class SaveToListActivity extends AppCompatActivity {

    private ImageView mBackButton;
    private ImageView mSaveButton;
    private RecyclerView mTravelListsRv;
    private ProgressBar mProgressBar;
    private Button mCreateListButton;

    private List<BucketList> mTravelLists;
    private TravelListsAdapter mAdapter;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_to_list);

        Intent intent = getIntent();
        item = Parcels.unwrap(intent.getParcelableExtra("item"));

        // Find views
        mBackButton = findViewById(R.id.backButton);
        mSaveButton = findViewById(R.id.saveButton);
        mTravelListsRv = findViewById(R.id.travelListsRv);
        mProgressBar = findViewById(R.id.progressBar);
        mCreateListButton = findViewById(R.id.createListButton);

        // Set up adapter for travel lists
        mTravelLists = new ArrayList<>();
        mAdapter = new TravelListsAdapter(mTravelLists, this);
        mTravelListsRv.setAdapter(mAdapter);
        mTravelListsRv.setLayoutManager(new LinearLayoutManager(this));

        queryLists();
        saveToSelectedLists();
        createListOnClicked();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                Toast.makeText(getApplicationContext(), "Saved to list", Toast.LENGTH_LONG).show();
                // Go through selected travel lists and add place to each list
                for (int i = 0; i < selectedLists.size(); i++) {
                    addItemToList(selectedLists.get(i));
                }
            }
        });
    }

    // Create a new instance of Item for each list and add that item to the list
    private void addItemToList(BucketList list) {
        item.setList(list);

        // Save to database
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
    }

    private void createListOnClicked() {
        mCreateListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewTravelListActivity.class);
                intent.putExtra("item", Parcels.wrap(item));
                startActivity(intent);
            }
        });
    }

}