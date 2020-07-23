package com.example.buckos.ui.feed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.models.Comment;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private ImageButton mPostButton;
    private EditText mAddCommentEditText;
    private RecyclerView mCommentsRecyclerView;
    private CommentsAdapter mCommentsAdapter;
    private ImageButton mBackButton;

    private Story mStory;
    private User mUser;
    private List<Comment> mCommentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Find views
        mPostButton = findViewById(R.id.postButton);
        mBackButton = findViewById(R.id.backButton);
        mAddCommentEditText = findViewById(R.id.addCommentEt);
        mCommentsRecyclerView = findViewById(R.id.commentsRv);

        // Retrieve the post object that comment will be added to
        Intent intent = getIntent();
        mStory = Parcels.unwrap(intent.getParcelableExtra("story"));

        // Set current user
        mUser = (User) ParseUser.getCurrentUser();

        // Retrieve the relations that hold pointers to comment in current story
        mCommentsList = new ArrayList<>(); // initialize the local comments list
        mCommentsAdapter = new CommentsAdapter(mCommentsList, this);
        mCommentsRecyclerView.setAdapter(mCommentsAdapter);
        mCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Find all comments in the post and display to view
        queryComments();

        // When user click Post, add comment to database and RecyclerView
        mPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComment(mAddCommentEditText.getText().toString());
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentsActivity.super.onBackPressed();
            }
        });
    }

    // Get all Comment objects in a post and add to local comments list
    private void queryComments() {
        // Specify which class to query
        ParseQuery query = ParseQuery.getQuery(Comment.class);
        // include the user object related to the story
        query.include(Comment.KEY_AUTHOR);
        // order the posts from newest to oldest
        query.orderByDescending(Comment.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                // clear out old items before fetching new ones on refresh
                mCommentsList.clear();
                mCommentsList.addAll(comments);
                // notify the adapter than new items have been added
                mCommentsAdapter.notifyDataSetChanged();
            }
        });
    }

    // This function create a comment and save to database.
    private void createComment(String body) {
        if (body.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new instance of comment
        final Comment comment = new Comment();

        // Set all properties of a comment
        comment.setBody(body);
        comment.setUser(mUser);
        comment.setStory(mStory);

        // Save the comment to database and update list of comments
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CommentsActivity.this,
                            "Issue publishing comment!", Toast.LENGTH_SHORT).show();
                }

                mCommentsList.add(comment);
                mStory.saveInBackground();

                mCommentsList.add(0, comment);
                mCommentsAdapter.notifyDataSetChanged();

            }
        });

        mAddCommentEditText.setText("");
    }

}