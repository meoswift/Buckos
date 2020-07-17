package com.example.buckos.main_screen.user_profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.User;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.BucketList;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.BucketListsAdapter;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.SwipeLeftToDelete;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UserProfileFragment extends Fragment {

    private static final int EDIT_PROFILE_REQ = 111;
    private RecyclerView mBucketListsRv;
    private TextView mDisplayNameTv;
    private TextView mBioTv;
    private Button mEditProfileBtn;
    private ProgressBar mProgressBar;

    private BucketListsAdapter mAdapter;

    User user;
    private List<BucketList> mBucketLists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        mBucketListsRv = view.findViewById(R.id.rvBucketLists);
        mDisplayNameTv = view.findViewById(R.id.displayNameTv);
        mBioTv = view.findViewById(R.id.bioTv);
        mProgressBar = view.findViewById(R.id.progressBar);
        mEditProfileBtn = view.findViewById(R.id.editProfileBtn);

        // Get current user to retrieve information
        user = (User) ParseUser.getCurrentUser();

        // Set up adapter for RecyclerView
        mBucketLists = new ArrayList<>();
        mAdapter = new BucketListsAdapter(getContext(), mBucketLists, view);
        mBucketListsRv.setAdapter(mAdapter);
        mBucketListsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeLeftToDelete(mAdapter, mBucketLists));
        itemTouchHelper.attachToRecyclerView(mBucketListsRv);

        // Populate user information and display their lists
        populateUserProfile();
        populateLists();

        // Navigate to Edit Profile screen on button clicked
        handleEditProfile();
    }

    // Populate views that comes with an user profile
    private void populateUserProfile() {
        mDisplayNameTv.setText(user.getName());
        mBioTv.setText(user.getBio());
    }

    // Display the bucket lists that user has created
    private void populateLists() {
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

                mBucketLists.addAll(objects);
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    // When user clicks Edit Profile button, takes them to Edit screen
    public void handleEditProfile() {
        mEditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQ);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Display the updated information of user profile after changes
        if (resultCode == RESULT_OK && requestCode == EDIT_PROFILE_REQ) {
            User user = Parcels.unwrap(data.getParcelableExtra("user"));
            mDisplayNameTv.setText(user.getName());
            mBioTv.setText(user.getBio());
        }
    }

}