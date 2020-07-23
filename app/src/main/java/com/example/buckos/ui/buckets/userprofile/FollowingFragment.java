package com.example.buckos.ui.buckets.userprofile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buckos.R;
import com.example.buckos.models.User;
import com.example.buckos.ui.explore.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    private RecyclerView mFollowingUsersRecyclerView;

    private List<User> mFollowingUsersList;
    private UsersAdapter mUsersAdapter;
    private User mUser;
    private ParseRelation<User> mFollowingUsers;

    public FollowingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        mFollowingUsersList = view.findViewById(R.id.followingUsersRv);

        // Initialize current user and following list
        mUser = (User) ParseUser.getCurrentUser();
        mFollowingUsers = mUser.getFollowingUsers();

        // Set up adapter for following list
        mFollowingUsersList = new ArrayList<>();
        mUsersAdapter = new UsersAdapter(mFollowingUsersList, getContext());
        mFollowingUsersRecyclerView.setAdapter(mUsersAdapter);
        mFollowingUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryFollowingUsers();
    }


    private void queryFollowingUsers() {
        ParseQuery<User> query = mFollowingUsers.getQuery();
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> following, ParseException e) {
                mFollowingUsersList.addAll(following);
                mUsersAdapter.notifyDataSetChanged();
            }
        });
    }
}