package com.example.buckos.app.create;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.app.buckets.BucketList;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

// Activity that allows user to create a new list. When done, user is directed to their Buckets
// tab that shows the new list.
public class NewListActivity extends AppCompatActivity {

    private EditText mListTitleEt;
    private EditText mListDescriptionEt;
    private TextView mCreateButton;
    private ImageView mCloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);

        mListTitleEt = findViewById(R.id.listTitleEt);
        mListDescriptionEt = findViewById(R.id.listDescriptionEt);
        mCreateButton = findViewById(R.id.createBtn);
        mCloseButton = findViewById(R.id.closeBtn);

        // Show cursor at description to hint user
        mListDescriptionEt.requestFocus();

        // Create new bucket and direct user to Buckets tab
        createBucketOnClicked();
        // Close Create new Bucket screen on click
        closeCreateOnClicked();
    }

    private void closeCreateOnClicked() {
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewListActivity.super.onBackPressed();
            }
        });
    }

    private void createBucketOnClicked() {
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listTitle = mListTitleEt.getText().toString();

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
        final BucketList list = new BucketList();

        // Set core properties of a bucket list object
        list.setName(mListTitleEt.getText().toString());
        list.setDescription(mListDescriptionEt.getText().toString());
        list.setAuthor(ParseUser.getCurrentUser());

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


}