package com.example.buckos.fragments;

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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.adapters.BucketListsAdapter;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {

    private RecyclerView mBucketListsRv;
    private TextView mDisplayNameTv;
    private TextView mBioTv;
    private Button mEditProfileBtn;
    BucketListsAdapter mAdapter;

    ParseUser user;
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

        // Get current user to retrieve information
        user = ParseUser.getCurrentUser();


        // Set up adapter for RecyclerView
        mBucketLists = new ArrayList<>();
        mAdapter = new BucketListsAdapter(getContext(), mBucketLists);
        mBucketListsRv.setAdapter(mAdapter);
        mBucketListsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Populate user information and display their lists
        populateUserProfile();
        populateLists();

    }

    private void populateUserProfile() {
        mDisplayNameTv.setText(user.getString(User.KEY_NAME));
        mBioTv.setText(user.getString(User.KEY_BIO));
    }

    private void populateLists() {
        // Specify which class to query
        ParseQuery<BucketList> query = ParseQuery.getQuery(BucketList.class);
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
            }
        });

    }
}