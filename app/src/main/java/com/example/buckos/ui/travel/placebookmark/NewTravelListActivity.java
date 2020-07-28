package com.example.buckos.ui.travel.placebookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Category;
import com.example.buckos.models.Item;
import com.example.buckos.ui.buckets.items.ListDetailsActivity;
import com.example.buckos.models.Place;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// Activity that allows user to create a new list and add the Place they bookmarked to that list
public class NewTravelListActivity extends AppCompatActivity {

    private EditText mListTitle;
    private EditText mListDescription;
    private TextView mCreateButton;
    private Spinner mCategorySpinner;

    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        mListTitle = findViewById(R.id.listTitleEt);
        mListDescription = findViewById(R.id.listDescriptionEt);
        mCreateButton = findViewById(R.id.createButton);
        mCategorySpinner = findViewById(R.id.categorySpinner);
        ImageButton closeButton = findViewById(R.id.closeButton);

        Intent intent = getIntent();
        mItem = Parcels.unwrap(intent.getParcelableExtra("item"));

        // set up category spinner
        queryCategories();

        // Create new list and add item to list on Create clicked
        handleOnCreateClicked();

        closeButton.setOnClickListener(new View.OnClickListener() {
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
                list.setCategory((Category) mCategorySpinner.getSelectedItem());

                list.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mItem.setList(list);
                        mItem.setCategory(list.getCategory());
                        mItem.saveInBackground();

                        Intent intent = new Intent(getApplicationContext(), ListDetailsActivity.class);
                        intent.putExtra("bucketList", Parcels.wrap(list));
                        startActivity(intent);
                        finish(); // prevent user from going back to create screen
                    }
                });
            }
        });
    }

    // get all categories from dtb and populate spinner
    private void queryCategories() {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.class);
        query.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> categories, ParseException e) {
                ArrayList<Category> mCategoryList = new ArrayList<>(categories);

                // set up adapter for spinner
                ArrayAdapter<Category> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, mCategoryList);
                mCategorySpinner.setAdapter(adapter);
            }
        });
    }

}