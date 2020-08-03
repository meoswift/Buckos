package com.example.buckos.ui.create;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Category;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// Activity that allows user to create a new list. When done, user is directed to their Buckets
// tab that shows the new list.
public class NewListActivity extends AppCompatActivity {

    private EditText mListTitleEditText;
    private EditText mListDescriptionEditText;
    private TextView mCreateButton;
    private ImageView mCloseButton;
    private Spinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        mListTitleEditText = findViewById(R.id.listTitleEt);
        mListDescriptionEditText = findViewById(R.id.listDescriptionEt);
        mCreateButton = findViewById(R.id.createButton);
        mCloseButton = findViewById(R.id.closeButton);
        mCategorySpinner = findViewById(R.id.categorySpinner);

        // Show cursor at description to hint user
        mListDescriptionEditText.requestFocus();

        // get all categories for a list
        queryCategories();

        // Create new bucket and direct user to Buckets tab
        createBucketOnClicked();
        // Close Create new Bucket screen on click
        closeCreateOnClicked();
    }

    private void closeCreateOnClicked() {
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }

    private void createBucketOnClicked() {
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listTitle = mListTitleEditText.getText().toString();

                if (listTitle.isEmpty()) {
                    Toast.makeText(NewListActivity.this, "Title cannot be empty!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                createList();
            }
        });
    }

    private void createList() {
        User user = (User) ParseUser.getCurrentUser();
        ParseRelation<Category> userInterests = user.getInterests();

        final BucketList list = new BucketList();
        Category selectedCategory = (Category) mCategorySpinner.getSelectedItem();

        // Set core properties of a bucket list object
        list.setName(mListTitleEditText.getText().toString());
        list.setDescription(mListDescriptionEditText.getText().toString());
        list.setAuthor(ParseUser.getCurrentUser());
        list.setCategory(selectedCategory);

        // if new category, add new interest to user
        userInterests.add(selectedCategory);
        user.saveInBackground();

        list.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // direct user to profile that shows the newly added list
                Intent intent = new Intent();
                intent.putExtra("list", Parcels.wrap(list));
                setResult(RESULT_OK, intent);
                finish();
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