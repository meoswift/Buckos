package com.example.buckos.ui.buckets.items;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

// Activity that allows user to edit the content of a list and set a category to the list
public class EditListActivity extends AppCompatActivity implements View.OnClickListener {

    private BucketList mBucketList;
    private EditText mListTitleEditText;
    private EditText mListDescriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // Find views
        mListTitleEditText = findViewById(R.id.listNameEt);
        mListDescriptionEditText = findViewById(R.id.listDescriptionEt);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton saveButton = findViewById(R.id.saveButton);

        // Populate views
        Intent intent = getIntent();
        mBucketList = Parcels.unwrap(intent.getParcelableExtra("list"));

        mListTitleEditText.setText(mBucketList.getName());
        mListDescriptionEditText.setText(mBucketList.getDescription());

        // Handle close or save button clicked
        backButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
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
}