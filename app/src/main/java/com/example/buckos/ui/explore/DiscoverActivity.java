package com.example.buckos.ui.explore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.buckos.R;
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
        ParseRelation<User> followingList = mCurrentUser.getFollowingUsers();

        ParseQuery<User> query = followingList.getQuery();
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> friends, ParseException e) {
                for (User friend : friends) {
                    querUserFollowingList(friend);
                }
            }
        });
    }

    // Get Following list of current user's friend
    private void querUserFollowingList(final User friend) {
        ParseRelation<User> followingList = friend.getFollowingUsers();

        // get all users that followed user is following
        ParseQuery<User> query = followingList.getQuery();
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> friendOfFriends, ParseException e) {
                for (User friendOfFriend : friendOfFriends) {
                    isFollowedByCurrentUser(friend, friendOfFriend);
                }
            }
        });
    }

    // Checks if anyone in the Following list of friend has been followed current user
    private void isFollowedByCurrentUser(final User friend, final User friendOfFriend) {
        ParseRelation<User> followingList = mCurrentUser.getFollowingUsers();

        // get users that current user is following
        ParseQuery<User> query = followingList.getQuery();
        // checks if current user has already followed friend of friend
        query.whereEqualTo(User.KEY_OBJECT_ID, friendOfFriend.getObjectId());
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (users.size() == 0) {
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