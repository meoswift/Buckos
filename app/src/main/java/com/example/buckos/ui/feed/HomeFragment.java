package com.example.buckos.ui.feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.buckos.R;
import com.example.buckos.models.Category;
import com.example.buckos.models.Follow;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseQuery.*;

// This fragment displays the Feed with posts from all users and a Welcome layout when users
// are new
public class HomeFragment extends Fragment {

    private StoriesAdapter mAdapter;
    private List<Story> mStories;

    private ProgressBar mHomeProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ConstraintLayout mWelcomeLayout;
    private RecyclerView mFollowSuggestionsRecyclerView;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views
        RecyclerView storiesRecyclerView = view.findViewById(R.id.storiesRv);
        mFollowSuggestionsRecyclerView = view.findViewById(R.id.userSuggestionsRv);
        mHomeProgressBar = view.findViewById(R.id.homeProgressBar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mWelcomeLayout = view.findViewById(R.id.welcomeLayout);

        // set up adapter for stories
        mStories = new ArrayList<>();
        mAdapter = new StoriesAdapter(mStories, getContext(), this);
        storiesRecyclerView.setAdapter(mAdapter);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set up refresher for layout
        setPullToRefreshContainer();
        // get all stories from dtb and display to feed
        queryStoriesFromFriends();
    }

    private void setPullToRefreshContainer() {
        // Setup refresh listener which triggers new data loading
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mStories.clear();
            queryStoriesFromFriends();
        });
    }

    // get all stories from people current user is following
    private void queryStoriesFromFriends() {
        final User user = (User) ParseUser.getCurrentUser();
        ParseRelation<User> friends = user.getFriends();
        ParseRelation<Category> interests = user.getInterests();

        // query compound user's friends and themselves
        ParseQuery<User> friendsQuery = friends.getQuery();
        ParseQuery<User> userQuery = getQuery(User.class);
        userQuery.whereEqualTo(User.KEY_OBJECT_ID, user.getObjectId());

        List<ParseQuery<User>> queries = new ArrayList<>();
        queries.add(friendsQuery);
        queries.add(userQuery);

        // query interests
        ParseQuery<Category> categoryQuery = interests.getQuery();
        ParseQuery<User> mainQuery = ParseQuery.or(queries);

        queryStories(mainQuery, categoryQuery);
    }

    // Get stories from specific user - relevant ones
    private void queryStories(ParseQuery queries, ParseQuery categoryQuery) {
        ParseQuery<Story> query = getQuery(Story.class);

        // include objects related to a story
        query.include(Story.KEY_AUTHOR);
        query.include(Story.KEY_ITEM);
        query.include(Story.KEY_LIST);
        query.include(Story.KEY_CATEGORY);

        // get all stories from friends that has same interests
        query.whereMatchesQuery(Story.KEY_AUTHOR, queries);
        query.whereMatchesQuery(Story.KEY_CATEGORY, categoryQuery);
        query.orderByDescending(Story.KEY_CREATED_AT);

        query.findInBackground((stories, e) -> {
            // add all relevant stories
            mStories.addAll(stories);
            queryIrrelevantStories(queries, categoryQuery);
        });
    }

    // Get stories from specific user - irrelevant ones
    private void queryIrrelevantStories(ParseQuery queries, ParseQuery categoryQuery) {
        ParseQuery<Story> query = getQuery(Story.class);

        // include objects related to a story
        query.include(Story.KEY_AUTHOR);
        query.include(Story.KEY_ITEM);
        query.include(Story.KEY_LIST);
        query.include(Story.KEY_CATEGORY);

        // get all stories from friends that has same interests
        query.whereMatchesQuery(Story.KEY_AUTHOR, queries);
        query.whereDoesNotMatchQuery(Story.KEY_CATEGORY, categoryQuery);
        query.orderByDescending(Story.KEY_CREATED_AT);

        query.findInBackground((stories, e) -> {
            // add all irrelevant stories
            mStories.addAll(stories);

            // query all photos from each story
            queryPhotos();
        });

    }

    // Get photos from each post
    private void queryPhotos() {
        // If there are no posts, also remove progress bar & refresher
        if (mStories.size() == 0) {
            mHomeProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
            displayWelcomeLayout();
            return;
        }

        mWelcomeLayout.setVisibility(View.GONE);

        for (Story story : mStories) {
            Item item = (Item) story.getItem();
            queryPhotosInStory(story, item);
        }
    }

    // For each story, get the photos included
    private void queryPhotosInStory(final Story story, Item item) {
        ParseQuery<Photo> query = getQuery(Photo.class);
        query.whereEqualTo(Story.KEY_ITEM, item);
        query.include(Photo.KEY_AUTHOR);
        query.findInBackground((photos, e) -> {
            story.setPhotosInStory(photos);
            mAdapter.notifyDataSetChanged();
            mHomeProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    // display the welcome layout w/ follow suggestions for new users
    private void displayWelcomeLayout() {
        mWelcomeLayout.setVisibility(View.VISIBLE);

        // set up adapter that displays follow suggestions for new user
        List<User> followSuggestions = new ArrayList<>();
        FollowSuggestionsAdapter adapter = new FollowSuggestionsAdapter(followSuggestions,
                                                                        getContext(), this);
        mFollowSuggestionsRecyclerView.setAdapter(adapter);
        mFollowSuggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mFollowSuggestionsRecyclerView.setItemViewCacheSize(20);

        queryFollowSuggestions(followSuggestions, adapter);
    }

    // get a list of follow suggestions for users who just created an account
    // suggestions include users who have interests, and not yet followed by current user
    private void queryFollowSuggestions(List<User> followSuggestions, FollowSuggestionsAdapter adapter) {
        User currentUser = (User) ParseUser.getCurrentUser();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.whereNotEqualTo(User.KEY_OBJECT_ID, currentUser.getObjectId());
        query.findInBackground((users, e) -> {
            for (User user : users) {
                // Only suggest users who have more than 1 interest & not followed yet
                ParseQuery<Category> queryInterests = user.getInterests().getQuery();
                queryInterests.countInBackground((count, e1) -> {
                    // if user has more than 1 interest
                    if (count > 0) {
                        // if user is in friends list
                        ParseQuery<User> friendsQuery = currentUser.getFriends().getQuery();
                        friendsQuery.findInBackground((friends, e2) -> {
                            if (!friends.contains(user)) {
                                followSuggestions.add(user);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
            }
        });
    }

}
