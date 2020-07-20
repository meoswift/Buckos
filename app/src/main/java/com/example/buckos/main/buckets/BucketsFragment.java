package com.example.buckos.main.buckets;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.main.MainActivity;
import com.example.buckos.main.buckets.userprofile.EditProfileActivity;
import com.example.buckos.main.buckets.userprofile.ProfileFragment;
import com.example.buckos.main.buckets.userprofile.User;
import com.example.buckos.main.create.NewListActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class BucketsFragment extends Fragment {
    private static final int NEW_LIST_REQUEST = 120;

    private RecyclerView mBucketListsRv;
    private ProgressBar mProgressBar;
    private ImageView mProfilePic;
    private Button mNewListButton;

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
        mBucketListsRv = view.findViewById(R.id.rvBucketLists);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProfilePic = view.findViewById(R.id.profilePic);
        mNewListButton = view.findViewById(R.id.newListButton);

        // Get current user to retrieve information
        user = (User) ParseUser.getCurrentUser();
        Glide.with(getContext()).load(getContext().getDrawable(R.drawable.bucket)).circleCrop().into(mProfilePic);

        // Set up adapter for RecyclerView
        mBucketLists = new ArrayList<>();
        mAdapter = new BucketListsAdapter(getContext(), mBucketLists, view);
        mBucketListsRv.setAdapter(mAdapter);
        mBucketListsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeLeftToDelete(mAdapter, mBucketLists));
        itemTouchHelper.attachToRecyclerView(mBucketListsRv);

        // Populate user information and display their lists
        populateLists();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_LIST_REQUEST && resultCode == RESULT_OK) {
            BucketList list = Parcels.unwrap(data.getParcelableExtra("list"));
            mBucketLists.add(0, list);
            mAdapter.notifyItemInserted(0);
        }
    }
}