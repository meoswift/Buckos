package com.example.buckos.main.travel.placebookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.main.buckets.BucketList;
import com.example.buckos.main.buckets.items.Item;
import com.example.buckos.main.buckets.items.ListDetailsActivity;
import com.example.buckos.main.travel.Place;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

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