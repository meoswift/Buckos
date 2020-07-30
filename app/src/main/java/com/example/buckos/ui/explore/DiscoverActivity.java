package com.example.buckos.ui.explore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DiscoverActivity extends AppCompatActivity {

    private List<User> mSuggestedUsersList;
    private SuggestedUsersAdapter mAdapter;
    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        RecyclerView suggestedUsersRecyclerView = findViewById(R.id.suggestedUsersRv);
        ImageButton backButton = findViewById(R.id.backButton);

        mSuggestedUsersList = new ArrayList<>();
        mAdapter = new SuggestedUsersAdapter(mSuggestedUsersList, this);
        suggestedUsersRecyclerView.setAdapter(mAdapter);
        suggestedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCurrentUser = (User) ParseUser.getCurrentUser();
        querySuggestedUsersList();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverActivity.super.onBackPressed();
            }
        });
    }

    // Get all users that current user is following
    private void querySuggestedUsersList() {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.include(Follow.KEY_TO);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                for (Follow follow : followList) {
                    User friend = follow.getTo();
                    queryUserFollowingList(friend);
                }
            }
        });
    }

    // Get Following list of current user's friend
    private void queryUserFollowingList(final User friend) {
        // get all users that followed user is following
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FROM, friend);
        query.whereNotEqualTo(Follow.KEY_TO, mCurrentUser);
        query.include(Follow.KEY_TO);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                for (Follow follow : followList) {
                    User friendOfFriend = follow.getTo();
                    isFollowedByCurrentUser(friend, friendOfFriend);
                }
            }
        });
    }

    // Checks if anyone in the Following list of friend has been followed current user
    private void isFollowedByCurrentUser(final User friend, final User friendOfFriend) {
        // get users that current user is following
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);

        // checks if current user has already followed friend of friend
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.whereEqualTo(Follow.KEY_TO, friendOfFriend);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                // have not yet followed friend of fried
                if (followList.size() == 0) {
                    if (!mSuggestedUsersList.contains(friendOfFriend)) {
                        mSuggestedUsersList.add(friendOfFriend);
                        friendOfFriend.setFollowedBy(friend.getUsername());
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }
}