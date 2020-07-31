package com.example.buckos.ui.explore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Category;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
//        querySuggestedUsersList();
        querySuggestionsByMutualFollowers();
        querySuggestionsByMutualInterests();

        backButton.setOnClickListener(v -> DiscoverActivity.super.onBackPressed());
    }
    


    private void querySuggestionsByMutualInterests() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereNotEqualTo(User.KEY_OBJECT_ID, mCurrentUser.getObjectId());
        query.findInBackground((users, e) -> {
            for (User user : users) {
                checkForMutualInterests(user);
            }
        });
    }


    // For each user, get a list of their interests.
    // Compare their interests list with current user's interests list.
    // If there are mutual interests between You and Random user, suggest that user
    @SuppressLint("DefaultLocale")
    private void checkForMutualInterests(User friend) {
        final List<Category> userInterests = new ArrayList<>();
        final List<Category> friendInterests = new ArrayList<>();

        ParseQuery<Category> query = mCurrentUser.getInterests().getQuery();

        query.findInBackground((interests, e) -> {
            userInterests.addAll(interests);
            ParseQuery<Category> queryFriend = friend.getInterests().getQuery();
            queryFriend.findInBackground((interestsList, e1) -> {
                friendInterests.addAll(interestsList);
                List<Category> common = new ArrayList(friendInterests);
                common.retainAll(userInterests);

                int commonInterestsCount = common.size();

                // if there's any common interests, add user to suggestions list
                if (commonInterestsCount > 0) {
                    if (!mSuggestedUsersList.contains(friend)) {
                        Category commonInterest = common.get(0);
                        if (commonInterestsCount == 1) {
                            friend.setSuggestionReason(String.format("You both like %s",
                                    commonInterest.getCategoryName()));
                        } else {
                            friend.setSuggestionReason(String.format("You both like %s + %d more",
                                    commonInterest.getCategoryName(), commonInterestsCount - 1));
                        }

                        mSuggestedUsersList.add(friend);
                        mAdapter.notifyDataSetChanged();
                        findViewById(R.id.noSuggestionsLabel).setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    // For each user user have not followed, get a list of their followers.
    // Compare their followers list with current user's followers list.
    // If there are mutual followers between You and Random user, suggest that user
    private void querySuggestionsByMutualFollowers() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereNotEqualTo(User.KEY_OBJECT_ID, mCurrentUser.getObjectId());
        query.findInBackground((users, e) -> {
            for (User user : users) {
                isFollowedByCurrentUser(user);
            }
        });

    }

    private void isFollowedByCurrentUser(User user) {
        // get users that current user is following
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);

        // checks if current user has already followed friend of friend
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.whereEqualTo(Follow.KEY_TO, user);
        query.findInBackground((followList, e) -> {
            if (followList.size() == 0) {
                checkMutualFollowers(user);
            }
        });
    }

    private void checkMutualFollowers(User user) {

    }


}