package com.example.buckos.ui.explore.othersprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.example.buckos.ui.feed.StoriesAdapter;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class StoriesProfileFragment extends Fragment {

    private RecyclerView mUserStoriesRecyclerView;
    private LinearLayout emptyLayout;
    private List<Story> mUserStories;
    private StoriesAdapter mStoriesAdapter;
    private ProgressBar mProgressBar;

    private User mUser;

    public StoriesProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get user
        Bundle bundle = getArguments();
        mUser = Parcels.unwrap(bundle.getParcelable("user"));

        // Find views
        mUserStoriesRecyclerView = view.findViewById(R.id.userStoriesRv);
        emptyLayout = view.findViewById(R.id.emptyLabel);
        mProgressBar = view.findViewById(R.id.profilePb);

        setAdapterForUserStories();
        queryStoriesFromUser();
    }

    private void setAdapterForUserStories() {
        mUserStories = new ArrayList<>();
        mStoriesAdapter = new StoriesAdapter(mUserStories, getContext(), this);
        mUserStoriesRecyclerView.setAdapter(mStoriesAdapter);
        mUserStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // Get stories from all users
    private void queryStoriesFromUser() {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        // include objects related to a story
        query.include(Story.KEY_AUTHOR);
        query.include(Story.KEY_ITEM);
        query.include(Story.KEY_LIST);
        query.include(Story.KEY_CATEGORY);
        // where author is current user and order by time created
        query.whereEqualTo(Story.KEY_AUTHOR, mUser);
        query.orderByDescending(Story.KEY_CREATED_AT);
        query.findInBackground((stories, e) -> {
            mUserStories.clear();
            for (int i = 0; i < stories.size(); i++) {
                Story story = stories.get(i);
                Item item = (Item) story.getItem();
                mUserStories.add(story);
                queryPhotosInStory(story, item);
            }

            if (stories.size() == 0) {
                emptyLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            } else {
                emptyLayout.setVisibility(View.GONE);
            }
        });
    }

    // For each story, get the photos included
    private void queryPhotosInStory(final Story story, Item item) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Story.KEY_ITEM, item);
        query.include(Photo.KEY_AUTHOR);
        query.findInBackground((photos, e) -> {
            story.setPhotosInStory(photos);
            mStoriesAdapter.notifyDataSetChanged();
            // call setRefreshing(false) to signal refresh has finished
            mProgressBar.setVisibility(View.GONE);
        });
    }
}