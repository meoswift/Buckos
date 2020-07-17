package com.example.buckos.main_screen.travel_explore.save_place_to_bucket_list;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.main_screen.travel_explore.list_of_places_in_city.Place;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.BucketList;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.bucket_list_items.Item;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.bucket_list_items.ListDetailsActivity;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;
import org.w3c.dom.Text;

public class NewTravelListActivity extends AppCompatActivity {

    private EditText mListTitle;
    private EditText mListDescription;
    private TextView mCreateButton;
    private ImageView mBackButton;

    private Place mPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_travel_list);

        mListTitle = findViewById(R.id.listNameEt);
        mListDescription = findViewById(R.id.listDescriptionEt);
        mCreateButton = findViewById(R.id.createButton);
        mBackButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        mPlace = Parcels.unwrap(intent.getParcelableExtra("place"));

        // Create new list and add item to list on Create clicked
        handleOnCreateClicked();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handleOnCreateClicked() {
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BucketList list = new BucketList();
                list.setName(mListTitle.getText().toString());
                list.setDescription(mListDescription.getText().toString());
                list.setAuthor(ParseUser.getCurrentUser());
                list.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        addPlaceToNewList(list);
                        Intent intent = new Intent(getApplicationContext(), ListDetailsActivity.class);
                        intent.putExtra("bucketList", Parcels.wrap(list));
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void addPlaceToNewList(BucketList list) {
        Item item = new Item();
        // Set core properties
        item.setName(mPlace.getName());
        item.setCompleted(false);
        item.setAuthor(ParseUser.getCurrentUser());
        item.setDescription(mPlace.getAddressName());
        item.setList(list);
        // Save to database
        item.saveInBackground();
    }
}