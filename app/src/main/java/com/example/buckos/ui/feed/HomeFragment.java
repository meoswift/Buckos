package com.example.buckos.ui.feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

// This fragment displays the Feed with posts from all users
public class HomeFragment extends Fragment {

    private RecyclerView mStoriesRecyclerView;
    private StoriesAdapter mAdapter;
    private List<Story> mStories;
    private ProgressBar mHomeProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        mStoriesRecyclerView = view.findViewById(R.id.storiesRv);
        mHomeProgressBar = view.findViewById(R.id.homeProgressBar);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        mStories = new ArrayList<>();
        mAdapter = new StoriesAdapter(mStories, getContext());
        mStoriesRecyclerView.setAdapter(mAdapter);
        mStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set up refresher for layout
        setPullToRefreshContainer();
        // get all stories from dtb and display to feed
        queryStories();
    }

    private void setPullToRefreshContainer() {
        // Setup refresh listener which triggers new data loading
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryStories();
            }
        });
    }

    // Get stories from all users
    private void queryStories() {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        query.include("author");
        query.include("item");
        query.include("list");
        query.orderByDescending(Story.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> stories, ParseException e) {
                mStories.clear();
                // in each story, get the photos attached
                for (int i = 0; i < stories.size(); i++) {
                    Story story = stories.get(i);
                    Item item = (Item) story.getItem();
                    queryPhotosInStory(story, item);
                }
            }
        });
    }

    // For each story, get the photos included
    private void queryPhotosInStory(final Story story, Item item) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Story.KEY_ITEM, item);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> photos, ParseException e) {
                story.setPhotosInStory(photos);
                mStories.add(story);
                mAdapter.notifyDataSetChanged();
                mHomeProgressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}