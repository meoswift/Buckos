package com.example.buckos.ui.buckets.items;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Category;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// Activity that allows user to edit the content of a list and set a category to the list
public class EditListActivity extends AppCompatActivity implements View.OnClickListener {

    private BucketList mBucketList;
    private EditText mListTitleEditText;
    private EditText mListDescriptionEditText;
    private Spinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // Find views
        mListTitleEditText = findViewById(R.id.listNameEt);
        mListDescriptionEditText = findViewById(R.id.listDescriptionEt);

        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton saveButton = findViewById(R.id.saveButton);
        mCategorySpinner = findViewById(R.id.categorySpinner);

        // Populate views
        Intent intent = getIntent();
        mBucketList = Parcels.unwrap(intent.getParcelableExtra("list"));

        mListTitleEditText.setText(mBucketList.getName());
        mListDescriptionEditText.setText(mBucketList.getDescription());

        // set up category spinner
        queryCategories();

        // Handle close or save button clicked
        backButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
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

                // default selection is selected category
                mCategorySpinner.setSelection(getSelectedCategory(mCategoryList));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.saveButton:
                saveListChanges();
                break;
        }
    }

    private void saveListChanges() {
        mBucketList.setName(mListTitleEditText.getText().toString());
        mBucketList.setDescription(mListDescriptionEditText.getText().toString());
        mBucketList.setCategory((Category) mCategorySpinner.getSelectedItem());

        mBucketList.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                intent.putExtra("list", Parcels.wrap(mBucketList));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // get the position of selected category in spinner
    private int getSelectedCategory(List<Category> categories) {
        String seletedCategoryId = mBucketList.getCategory().getObjectId();

        for (Category category : categories) {
            if (category.getObjectId().equals(seletedCategoryId))
                return categories.indexOf(category);
        }

        return -1;
    }

}