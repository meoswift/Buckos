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
import com.example.buckos.ui.explore.search.UsersAdapter;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

// Fragment to display current user's following list
public class FollowingFragment extends Fragment implements View.OnClickListener {

    private List<User> mFollowingUsersList;
    private UsersAdapter mUsersAdapter;
    private User mUser;

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
        RecyclerView followingUsersRecyclerView = view.findViewById(R.id.followingUsersRv);
        ImageButton backButton = view.findViewById(R.id.backButton);

        // Initialize user
        Bundle bundle = this.getArguments();
        mUser = Parcels.unwrap(bundle.getParcelable("user"));

        // Set up adapter for following list
        mFollowingUsersList = new ArrayList<>();
        mUsersAdapter = new UsersAdapter(mFollowingUsersList, getContext(), this);
        followingUsersRecyclerView.setAdapter(mUsersAdapter);
        followingUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryFollowingUsers();

        backButton.setOnClickListener(this);
    }


    // get users that current user is following
    private void queryFollowingUsers() {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FROM, mUser);
        query.include(Follow.KEY_TO);
        query.findInBackground((followList, e) -> {
            for (Follow relationship : followList) {
                User followedUser = relationship.getTo();
                mFollowingUsersList.add(followedUser);
            }

            mUsersAdapter.notifyDataSetChanged();
        });
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backButton)
            getParentFragmentManager().popBackStack();
    }
}