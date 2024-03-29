package com.example.buckos.ui.buckets.userprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.example.buckos.ui.authentication.LoginActivity;
import com.example.buckos.ui.feed.StoriesAdapter;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// Fragment to display current user's information: display name, bio, and posts. User can log out
// or navigate to Edit profile screen from this fragment.
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int EDIT_PROFILE_REQ = 111;
    private NestedScrollView mProfileLayout;
    private TextView mDisplayNameTextView;
    private TextView mBioTextView;
    private TextView mUsernameTextView;
    private ImageView mProfilePicImageView;
    private Toolbar mProfileToolbar;
    private RecyclerView mUserStoriesRecyclerView;
    private SwipeRefreshLayout swipeContainer;
    private TextView mFollowingTextView;
    private TextView mFollowersTextView;
    private TextView mStoriesTextView;
    private LinearLayout mEmptyLayout;
    private ProgressBar mProgressBar;


    private User user;
    private List<Story> mUserStories;
    private StoriesAdapter mStoriesAdapter;

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

        // set the status bar color to white after changing
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));

        // Get current user & following list
        user = (User) ParseUser.getCurrentUser();

        // Find views
        mProfileLayout = view.findViewById(R.id.profileLayout);
        mDisplayNameTextView = view.findViewById(R.id.displayNameTv);
        mBioTextView = view.findViewById(R.id.bioTv);
        mUsernameTextView = view.findViewById(R.id.usernameTv);
        mProfilePicImageView = view.findViewById(R.id.authorProfilePic);
        mProfileToolbar = view.findViewById(R.id.profileToolbar);
        mUserStoriesRecyclerView = view.findViewById(R.id.userStoriesRv);
        swipeContainer = view.findViewById(R.id.swipeRefreshLayout);
        mFollowingTextView = view.findViewById(R.id.followingCountTv);
        mFollowersTextView = view.findViewById(R.id.followersCountTv);
        mStoriesTextView = view.findViewById(R.id.storiesCountTv);
        mEmptyLayout = view.findViewById(R.id.emptyLabel);
        mProgressBar = view.findViewById(R.id.profilePb);

        ImageView backButton = view.findViewById(R.id.backButton);
        LinearLayout followingLabel = view.findViewById(R.id.following);
        LinearLayout followersLabel = view.findViewById(R.id.followers);


        // pull to refresh
        setPullToRefreshContainer();

        // populate info and user stories
        populateUserProfile();

        // 2 options: edit profile - log out
        handleProfileMenuClicked();

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
        mBioTextView.setText(user.getBio());
        mUsernameTextView.setText("@" + user.getUsername());
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
        query.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> stories, ParseException e) {
                mUserStories.clear();
                for (int i = 0; i < stories.size(); i++) {
                    Story story = stories.get(i);
                    Item item = (Item) story.getItem();
                    mUserStories.add(story);
                    queryPhotosInStory(story, item);
                }

                if (stories.size() == 0) {
                    mEmptyLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mProfileLayout.setVisibility(View.VISIBLE);
                } else {
                    mEmptyLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    // For each story, get the photos included
    private void queryPhotosInStory(final Story story, Item item) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Story.KEY_ITEM, item);
        query.include(Photo.KEY_AUTHOR);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> photos, ParseException e) {
                story.setPhotosInStory(photos);
                mStoriesAdapter.notifyDataSetChanged();
                // call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
                mProfileLayout.setVisibility(View.VISIBLE);
            }
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

    // Set the number of following
    private void setStoriesCount() {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        // include objects related to a story
        query.whereEqualTo(Story.KEY_AUTHOR, user);
        query.countInBackground((count, e) -> {
            mStoriesTextView.setText(String.valueOf(count));
        });
    }


    // When user click edit or Log out, executes appropriate task
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