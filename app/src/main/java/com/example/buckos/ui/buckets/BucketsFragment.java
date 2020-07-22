package com.example.buckos.ui.buckets;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.ui.buckets.userprofile.ProfileFragment;
import com.example.buckos.ui.buckets.userprofile.User;
import com.example.buckos.ui.create.NewListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// Fragment to display current user's bucket lists. User can create new bucket list and
// navigate to their profile in this fragment.
public class BucketsFragment extends Fragment {
    private static final int NEW_LIST_REQUEST = 120;
    public static final int POST_ITEM_REQUEST = 222;

    private RecyclerView mBucketListsRecyclerView;
    private ProgressBar mProgressBar;
    private ImageView mProfilePic;
    private Button mNewListButton;
    private BottomNavigationView mBottomNavigationView;

    private User user;
    private List<BucketList> mBucketLists;
    private BucketListsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buckets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        mBucketListsRecyclerView = view.findViewById(R.id.rvBucketLists);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProfilePic = view.findViewById(R.id.profilePic);
        mNewListButton = view.findViewById(R.id.newListButton);
        mBottomNavigationView = view.getRootView().findViewById(R.id.bottomNavigation);

        // Get current user to retrieve information
        user = (User) ParseUser.getCurrentUser();
        setProfilePic();

        // Set up adapter for RecyclerView
        mBucketLists = new ArrayList<>();
        mAdapter = new BucketListsAdapter(getContext(), mBucketLists, view, this);
        mBucketListsRecyclerView.setAdapter(mAdapter);
        mBucketListsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeLeftToDelete(mAdapter, mBucketLists));
        itemTouchHelper.attachToRecyclerView(mBucketListsRecyclerView);

        // Populate user information and display their lists
        populateLists(mBucketLists, mAdapter);
        // Open user profile on profile pic clicked
        openUserProfile();
        // Open Create Bucket screen on New Bucket clicked
        openCreateBucketOnClicked();

    }

    private void openCreateBucketOnClicked() {
        mNewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewListActivity.class);
                startActivityForResult(intent, NEW_LIST_REQUEST);
            }
        });
    }

    private void openUserProfile() {
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragment fragment = new ProfileFragment();

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.your_placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // Display the bucket lists that user has created
    public void populateLists(final List<BucketList> bucketLists, final BucketListsAdapter adapter) {
        // Specify which class to query
        ParseQuery<BucketList> query = ParseQuery.getQuery(BucketList.class);
        // get only lists that belong to current users
        query.whereEqualTo(BucketList.KEY_AUTHOR, user);
        // order the posts from newest to oldest
        query.orderByDescending(BucketList.KEY_CREATED_AT);
        // start an asynchronous call for lists
        query.findInBackground(new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> objects, ParseException e) {
                if (e != null) {
                    Log.d("UserProfileFragment", "Issue with querying posts" + e);
                    return;
                }

                bucketLists.addAll(objects);
                adapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // add new bucket to list and update adapter
        if (requestCode == NEW_LIST_REQUEST && resultCode == RESULT_OK) {
            BucketList list = Parcels.unwrap(data.getParcelableExtra("list"));
            mBucketLists.add(0, list);
            mAdapter.notifyItemInserted(0);
        }

        // direct user to home feed after they post a completed item
        if (requestCode == BucketsFragment.POST_ITEM_REQUEST && resultCode == RESULT_OK) {
            mBottomNavigationView.setSelectedItemId(R.id.action_home);
        }

    }

    // Set profile pic with either file from database or default image
    private void setProfilePic() {
        ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

        if (image != null)
            Glide.with(getContext()).load(image.getUrl()).circleCrop().into(mProfilePic);
        else
            Glide.with(getContext()).load(R.drawable.ic_launcher_background)
                    .circleCrop().into(mProfilePic);
    }

    private void setTransitionSharedElements(Fragment profileFragment) {
        Transition changeTransform = TransitionInflater.from(getContext()).
                inflateTransition(R.transition.change_image_transform);
        Transition transform = TransitionInflater.from(getContext()).
                inflateTransition(android.R.transition.no_transition);
        profileFragment.setSharedElementEnterTransition(changeTransform);
        profileFragment.setEnterTransition(transform);
    }

}