package com.example.buckos.ui.travel.placebookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.example.buckos.ui.buckets.items.ListDetailsActivity;
import com.example.buckos.models.Place;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

// Activity that allows user to create a new list and add the Place they bookmarked to that list
public class NewTravelListActivity extends AppCompatActivity {

    private EditText mListTitle;
    private EditText mListDescription;
    private TextView mCreateButton;
    private ImageView mBackButton;

    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_travel_list);

        mListTitle = findViewById(R.id.listNameEt);
        mListDescription = findViewById(R.id.listDescriptionEt);
        mCreateButton = findViewById(R.id.createButton);
        mBackButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        mItem = Parcels.unwrap(intent.getParcelableExtra("item"));

        // Create new list and add item to list on Create clicked
        handleOnCreateClicked();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTravelListActivity.super.onBackPressed();
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
                        finish(); // prevent user from going back to create screen
                    }
                });
            }
        });
    }

    private void addPlaceToNewList(BucketList list) {

        mItem.setList(list);
        // Save to database
        mItem.saveInBackground();
    }
}