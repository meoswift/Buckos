package com.example.buckos.main.feed;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.buckos.R;
import com.example.buckos.main.buckets.items.Item;
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

        mStories = new ArrayList<>();
        mAdapter = new StoriesAdapter(mStories, getContext());
        mStoriesRecyclerView.setAdapter(mAdapter);
        mStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get all stories from dtb and display to feed
        queryStories();
    }

    private void queryStories() {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        query.include("author");
        query.orderByDescending(Story.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> objects, ParseException e) {
                mStories.addAll(objects);
                mHomeProgressBar.setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}