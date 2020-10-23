package com.example.buckos.ui.explore.othersprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.example.buckos.ui.authentication.LoginActivity;
import com.example.buckos.ui.buckets.userprofile.EditProfileActivity;
import com.example.buckos.ui.buckets.userprofile.FollowersFragment;
import com.example.buckos.ui.buckets.userprofile.FollowingFragment;
import com.example.buckos.ui.feed.StoriesAdapter;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// Fragment that display a user profile that is not current user's profile
// Display stories, followings, followers, but no Edit profile menu
public class OthersProfileFragment extends Fragment implements View.OnClickListener{

    private TextView mDisplayNameTextView;
    private TextView mBioTextView;
    private TextView mUsernameTextView;
    private ImageView mProfilePicImageView;
    private RecyclerView mUserStoriesRecyclerView;
    private SwipeRefreshLayout swipeContainer;
    private TextView mFollowingTextView;
    private TextView mFollowersTextView;
    private TextView mStoriesTextView;
    private LinearLayout emptyLayout;

    private User user;
    private List<Story> mUserStories;
    private StoriesAdapter mStoriesAdapter;

    public OthersProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_others_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set the status bar color to white after changing
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));

        // Get current user & following list
        Bundle bundle = this.getArguments();
        user = Parcels.unwrap(bundle.getParcelable("user"));

        // Find views
        mDisplayNameTextView = view.findViewById(R.id.displayNameTv);
        mBioTextView = view.findViewById(R.id.bioTv);
        mUsernameTextView = view.findViewById(R.id.usernameTv);
        mProfilePicImageView = view.findViewById(R.id.authorProfilePic);
        mUserStoriesRecyclerView = view.findViewById(R.id.userStoriesRv);
        swipeContainer = view.findViewById(R.id.swipeRefreshLayout);
        mFollowingTextView = view.findViewById(R.id.followingCountTv);
        mFollowersTextView = view.findViewById(R.id.followersCountTv);
        mStoriesTextView = view.findViewById(R.id.storiesCountTv);
        emptyLayout = view.findViewById(R.id.emptyLabel);

        ImageView backButton = view.findViewById(R.id.backButton);
        LinearLayout followingLabel = view.findViewById(R.id.following);
        LinearLayout followersLabel = view.findViewById(R.id.followers);

        // pull to refresh
        setPullToRefreshContainer();

        // populate info and user stories
        populateUserProfile();

        backButton.setOnClickListener(this);
        followingLabel.setOnClickListener(this);
        followersLabel.setOnClickListener(this);
    }

    private void setPullToRefreshContainer() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryStoriesFromUser();
            }
        });
    }

    // Populate views that comes with an user profile
    private void populateUserProfile() {
        setProfilePic();
        mDisplayNameTextView.setText(user.getName());
        mUsernameTextView.setText(String.format("@%s", user.getUsername()));
        mBioTextView.setText(user.getBio());
        setAdapterForUserStories();

        new Thread(new Runnable() {
            public void run(){
                setFollowingCount();
                setFollowersCount();
                setStoriesCount();
                queryStoriesFromUser();
            }
        }).start();
    }

    private void setAdapterForUserStories() {
        mUserStories = new ArrayList<>();
        mStoriesAdapter = new StoriesAdapter(mUserStories, getContext(), this);
        mUserStoriesRecyclerView.setAdapter(mStoriesAdapter);
        mUserStoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryStoriesFromUser();
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
        query.whereEqualTo(Story.KEY_AUTHOR, user);
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
            swipeContainer.setRefreshing(false);
        });
    }

    // Set profile pic with either file from database or default image
    private void setProfilePic() {
        ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(getContext()).load(image.getUrl()).circleCrop().into(mProfilePicImageView);
        else
            Glide.with(getContext()).load(R.drawable.no_profile_pic)
                    .circleCrop().into(mProfilePicImageView);
    }

    // Set the number of following
    private void setStoriesCount() {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        // include objects related to a story
        query.whereEqualTo(Story.KEY_AUTHOR, user);
        query.countInBackground((count, e) -> {
            mStoriesTextView.setText(String.valueOf(count));
        });
    }

    // Set the number of following
    private void setFollowingCount() {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FROM, user);
        query.countInBackground((count, e) -> {
            mFollowingTextView.setText(String.valueOf(count));
        });
    }

    // Set the number of followers
    private void setFollowersCount() {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_TO, user);
        query.countInBackground((count, e) -> {
            mFollowersTextView.setText(String.valueOf(count));
        });
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", Parcels.wrap(user));

        switch (v.getId()) {

            case R.id.backButton:
                getActivity().onBackPressed();
                break;

            case R.id.following:
                Fragment followingFragment = new FollowingFragment();
                followingFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.your_placeholder, followingFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.followers:
                Fragment followersFragment = new FollowersFragment();
                followersFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.your_placeholder, followersFragment)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mStoriesAdapter.notifyDataSetChanged();
    }
}