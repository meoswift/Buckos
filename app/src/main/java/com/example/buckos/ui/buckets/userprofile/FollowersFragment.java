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
import android.widget.ImageButton;

import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.example.buckos.ui.explore.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment implements View.OnClickListener {

    private List<User> mFollowersList;
    private UsersAdapter mUsersAdapter;
    private User mUser;

    public FollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        RecyclerView followersRecyclerView = view.findViewById(R.id.followersRv);
        ImageButton backButton = view.findViewById(R.id.backButton);

        // Initialize current user and following list
        mUser = (User) ParseUser.getCurrentUser();

        // Set up adapter for following list
        mFollowersList = new ArrayList<>();
        mUsersAdapter = new UsersAdapter(mFollowersList, getContext());
        followersRecyclerView.setAdapter(mUsersAdapter);
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryFollowers();

        backButton.setOnClickListener(this);
    }


    private void queryFollowers() {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_TO, mUser);
        query.include(Follow.KEY_FROM);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                for (Follow relationship : followList) {
                    User follower = relationship.getFrom();
                    mFollowersList.add(follower);
                }

                mUsersAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton)
            getParentFragmentManager().popBackStack();
    }
}