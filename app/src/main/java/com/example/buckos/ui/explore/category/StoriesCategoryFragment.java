package com.example.buckos.ui.explore.category;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.Category;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.ui.feed.StoriesAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.parse.ParseQuery.getQuery;

// Fragment that displays the user stories of a specific category
public class StoriesCategoryFragment extends Fragment {

    private Category mCategory;
    private List<Story> mStories;
    private StoriesAdapter mAdapter;
    private ProgressBar mStoriesCategoryProgressBar;

    public StoriesCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stories_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views
        mStoriesCategoryProgressBar = view.findViewById(R.id.storiesCategoryProgressBar);

        // get the selected category
        Bundle bundle = getArguments();
        mCategory = Parcels.unwrap(bundle.getParcelable("category"));

        // set up adapter and recyclerview
        mStories = new ArrayList<>();
        mAdapter = new StoriesAdapter(mStories, getContext(), this);
        RecyclerView storiesRecyclerView = view.findViewById(R.id.storiesRv);
        storiesRecyclerView.setAdapter(mAdapter);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get all stories of this category
        queryStoriesOfCategory();
    }

    private void queryStoriesOfCategory() {
        ParseQuery<Story> queryStory = ParseQuery.getQuery(Story.class);
        ParseQuery<Category> categoryQuery = ParseQuery.getQuery(Category.class);

        // get the category object from category name
        categoryQuery.whereEqualTo(Category.KEY_NAME, mCategory.getName());
        categoryQuery.getFirstInBackground((category, e) -> {
            // query all stories of this category
            queryStory.whereEqualTo(Story.KEY_CATEGORY, category);

            // include objects related to a story
            queryStory.include(Story.KEY_AUTHOR);
            queryStory.include(Story.KEY_ITEM);
            queryStory.include(Story.KEY_LIST);
            queryStory.include(Story.KEY_CATEGORY);
            queryStory.orderByDescending(Story.KEY_CREATED_AT);

            queryStory.findInBackground((stories, e1) -> {
                mStories.addAll(stories);
                queryPhotos();
            });
        });
    }

    // Get photos from each post
    private void queryPhotos() {
        for (Story story : mStories) {
            Item item = (Item) story.getItem();
            queryPhotosInStory(story, item);
        }
    }

    // For each story, get the photos included
    private void queryPhotosInStory(final Story story, Item item) {
        ParseQuery<Photo> query = getQuery(Photo.class);
        query.whereEqualTo(Story.KEY_ITEM, item);
        query.include(Story.KEY_AUTHOR);
        query.findInBackground((photos, e) -> {
            story.setPhotosInStory(photos);
            mAdapter.notifyDataSetChanged();
            mStoriesCategoryProgressBar.setVisibility(View.GONE);
        });
    }
}