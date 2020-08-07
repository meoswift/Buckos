package com.example.buckos.ui.explore.othersprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.example.buckos.ui.buckets.userprofile.FollowersFragment;
import com.example.buckos.ui.buckets.userprofile.FollowingFragment;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.parceler.Parcels;

public class OthersProfileFragment extends Fragment implements View.OnClickListener{

    private TextView mDisplayNameTextView;
    private TextView mBioTextView;
    private TextView mUsernameTextView;
    private ImageView mProfilePicImageView;
    private TextView mFollowingTextView;
    private TextView mFollowersTextView;
    private TextView mStoriesTextView;
    private TabLayout mTabLayout;
    private NestedScrollView mProfileLayout;

    private Fragment mFragment;
    private User user;
    private Bundle mBundle;

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

        // Get current user & following list
        mBundle = this.getArguments();
        user = Parcels.unwrap(mBundle.getParcelable("user"));

        // Find views
        mDisplayNameTextView = view.findViewById(R.id.displayNameTv);
        mBioTextView = view.findViewById(R.id.bioTv);
        mUsernameTextView = view.findViewById(R.id.usernameTv);
        mProfilePicImageView = view.findViewById(R.id.authorProfilePic);
        mFollowingTextView = view.findViewById(R.id.followingCountTv);
        mFollowersTextView = view.findViewById(R.id.followersCountTv);
        mStoriesTextView = view.findViewById(R.id.storiesCountTv);
        mProfileLayout = view.findViewById(R.id.profileLayout);
        mTabLayout = view.findViewById(R.id.tabLayout);

        ImageView backButton = view.findViewById(R.id.backButton);
        LinearLayout followingLabel = view.findViewById(R.id.following);
        LinearLayout followersLabel = view.findViewById(R.id.followers);

        // populate info and user stories
        populateUserProfile();

        // set default tab - stories
        inflateDefaultTab();
        // update tab on selection
        updateTabOnSelected();

        backButton.setOnClickListener(this);
        followingLabel.setOnClickListener(this);
        followersLabel.setOnClickListener(this);
    }

    // Populate views that comes with an user profile
    private void populateUserProfile() {
        setProfilePic();
        mDisplayNameTextView.setText(user.getName());
        mBioTextView.setText(user.getBio());
        mUsernameTextView.setText(String.format("@%s", user.getUsername()));

        new Thread(new Runnable() {
            public void run(){
                setFollowingCount();
                setFollowersCount();
                setStoriesCount();
                mProfileLayout.setVisibility(View.VISIBLE);
            }
        }).start();
    }


    private void inflateDefaultTab() {
        mFragment = new StoriesProfileFragment(); // Default tab
        mFragment.setArguments(mBundle);
        updateTabWithFragment();
    }

    // When using swipes between two tabs, update the according fragment
    public void updateTabOnSelected() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                // Open the fragment depending on which tab was chosen
                switch (position) {
                    case 0:
                        mFragment = new StoriesProfileFragment();
                        mFragment.setArguments(mBundle);
                        break;
                    case 1:
                        break;
                }

                updateTabWithFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Function to replace layout with the Tab selected
    private void updateTabWithFragment() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_placeholder, mFragment)
                .addToBackStack(null)
                .commit();
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


}