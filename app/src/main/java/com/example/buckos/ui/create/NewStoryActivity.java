package com.example.buckos.ui.create;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// Activity that display a list of possible stories that user can post to Feed
// User can select one story suggestion at a time
public class NewStoryActivity extends AppCompatActivity implements View.OnClickListener {

    private StorySuggestionsAdapter mAdapter;
    private List<Item> mStorySuggestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);

        // Find views
        ImageButton closeButton = findViewById(R.id.closeButton);
        TextView postButton = findViewById(R.id.postButton);

        // set up adapter
        mStorySuggestions = new ArrayList<>();
        RecyclerView storySuggestionsRecyclerView = findViewById(R.id.storySuggestionsRv);
        mAdapter = new StorySuggestionsAdapter(this, mStorySuggestions);
        storySuggestionsRecyclerView.setAdapter(mAdapter);
        storySuggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // get 20 (?) story suggestions
        queryStorySuggestions();

        closeButton.setOnClickListener(this);
        postButton.setOnClickListener(this);
    }

    // Get stories from current user for suggestions
    private void queryStorySuggestions() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        // include objects related to a story
        query.include(Item.KEY_AUTHOR);
        query.include(Item.KEY_LIST);
        query.whereEqualTo(Item.KEY_COMPLETED, true);
        query.whereEqualTo(Item.KEY_AUTHOR, ParseUser.getCurrentUser());
        // order by time created
        query.orderByDescending(Story.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> items, ParseException e) {
                // in each story, get the photos attached
                for (int i = 0; i < items.size(); i++) {
                    Item story = items.get(i);
                    mStorySuggestions.add(story);
                    mAdapter.notifyDataSetChanged();
                    findViewById(R.id.createStoryLabel).setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                finish();
                break;
            case R.id.postButton:
                Item item = mAdapter.getSelectedStory();
                createStoryFromSelectedItem(item);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
        }

    }

    private Story createStoryFromSelectedItem(Item item) {
        Story story = new Story();

        // set core properties of a story
        story.setAuthor(ParseUser.getCurrentUser());
        story.setTitle(item.getName());
        story.setDescription(item.getDescription());
        story.setItem(item);
        story.setList(item.getList());
        story.setCategory(item.getCategory());

        // save in database
        story.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(NewStoryActivity.this, "New story posted!", Toast.LENGTH_SHORT).show();
            }
        });

        return story;
    }
}