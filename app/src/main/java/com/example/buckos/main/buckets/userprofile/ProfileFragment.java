package com.example.buckos.main.buckets.userprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.authentication.LoginActivity;
import com.example.buckos.main.buckets.items.Item;
import com.example.buckos.main.buckets.items.content.Photo;
import com.example.buckos.main.feed.StoriesAdapter;
import com.example.buckos.main.feed.Story;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// Fragment to display current user's information: display name, bio, and posts. User can log out
// or navigate to Edit profile screen from this fragment.
public class ProfileFragment extends Fragment {

    private static final int EDIT_PROFILE_REQ = 111;
    private TextView mDisplayNameTextView;
    private TextView mBioTextView;
    private ImageView mProfilePicImageView;
    private Toolbar mProfileToolbar;
    private ImageView mBackButton;
    private StoriesAdapter mStoriesAdapter;
    private RecyclerView mUserStoriesRecyclerView;

    private User user;
    private List<Story> mUserStories;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user
        user = (User) ParseUser.getCurrentUser();

        // Find views
        mDisplayNameTextView = view.findViewById(R.id.displayNameTv);
        mBioTextView = view.findViewById(R.id.bioTv);
        mProfilePicImageView = view.findViewById(R.id.profilePicIv);
        mProfileToolbar = view.findViewById(R.id.profileToolbar);
        mBackButton = view.findViewById(R.id.backButton);
        mUserStoriesRecyclerView = view.findViewById(R.id.userStoriesRv);


        populateUserProfile();
        handleProfileMenuClicked();
        handleBackButtonClicked();
    }

    // Populate views that comes with an user profile
    private void populateUserProfile() {
        mDisplayNameTextView.setText(user.getName());
        mBioTextView.setText(user.getBio());
        setProfilePic();

        displayUserStories();
    }

    private void displayUserStories() {
        mUserStories = new ArrayList<>();
        mStoriesAdapter = new StoriesAdapter(mUserStories, getContext());
        mUserStoriesRecyclerView.setAdapter(mStoriesAdapter);
        mUserStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryStoriesFromUser();
    }

    // Get stories from all users
    private void queryStoriesFromUser() {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        query.include("author");
        query.include("item");
        query.whereEqualTo(Story.KEY_AUTHOR, user);
        query.orderByDescending(Story.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> stories, ParseException e) {
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
                mUserStories.add(story);
                mStoriesAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Display the updated information of user profile after changes
        if (resultCode == RESULT_OK && requestCode == EDIT_PROFILE_REQ) {
            User user = (User) ParseUser.getCurrentUser();
            mDisplayNameTextView.setText(user.getName());
            mBioTextView.setText(user.getBio());
            setProfilePic();
        }
    }

    // Set profile pic with either file from database or default image
    private void setProfilePic() {
        ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(getContext()).load(image.getUrl()).circleCrop().into(mProfilePicImageView);
        else
            Glide.with(getContext()).load(R.drawable.ic_launcher_background)
                    .circleCrop().into(mProfilePicImageView);
    }


    private void handleBackButtonClicked() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void handleProfileMenuClicked() {
        mProfileToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_edit) {
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivityForResult(intent, EDIT_PROFILE_REQ);
                } else {
                    ParseUser.logOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });
    }


}