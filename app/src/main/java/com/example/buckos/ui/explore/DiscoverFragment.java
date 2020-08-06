package com.example.buckos.ui.explore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Category;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.example.buckos.ui.explore.search.SuggestedUsersAdapter;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// Activity that displays following suggestions to user depending on who they've followed and
// what they are interested in
public class DiscoverFragment extends Fragment {

    private List<User> mSuggestedUsersList;
    private SuggestedUsersAdapter mAdapter;
    private User mCurrentUser;
    private ProgressBar mProgressBar;

    public DiscoverFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_discover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView suggestedUsersRecyclerView = view.findViewById(R.id.suggestedUsersRv);
        ImageButton backButton = view.findViewById(R.id.backButton);
        mProgressBar = view.findViewById(R.id.discoverPb);

        mSuggestedUsersList = new ArrayList<>();
        mAdapter = new SuggestedUsersAdapter(mSuggestedUsersList, getContext(), this);
        suggestedUsersRecyclerView.setAdapter(mAdapter);
        suggestedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCurrentUser = (User) ParseUser.getCurrentUser();

        querySuggestionsByNumFriends();

        backButton.setOnClickListener(v -> getActivity().onBackPressed());

    }

    private void querySuggestionsByNumFriends() {
        ParseQuery<User> query = mCurrentUser.getFriends().getQuery();
        query.countInBackground((friends, e) -> {
            // if user haven't followed anyone
            if (friends == 0) {
                queryInitialSuggestions();
            } else {
                querySuggestions();
            }
        });
    }

    // suggest all users who have interests
    private void queryInitialSuggestions() {
        User currentUser = (User) ParseUser.getCurrentUser();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.whereNotEqualTo(User.KEY_OBJECT_ID, currentUser.getObjectId());
        query.findInBackground((users, e) -> {
            mSuggestedUsersList.clear();
            for (User user : users) {
                ParseQuery<Category> interestsQuery = user.getInterests().getQuery();
                interestsQuery.getFirstInBackground((interest, e1) -> {
                    if (interest != null) {
                        user.setSuggestionReason("Interested in " + interest.getCategoryName());
                        mSuggestedUsersList.add(user);
                        mAdapter.notifyItemInserted(mSuggestedUsersList.size() - 1);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }



    // get all users who are not yourself
    private void querySuggestions() {
        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereNotEqualTo(User.KEY_OBJECT_ID, mCurrentUser.getObjectId());
        query.findInBackground((users, e) -> {
            for (User user : users) {
                checkIfFollowedByCurrentUser(user);
            }
        });
    }

    // checks if current user has followed the user or not
    // if not, checks if user shares mutual followers or interests
    private void checkIfFollowedByCurrentUser(User user) {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.whereEqualTo(Follow.KEY_TO, user);

        query.findInBackground((follows, e) -> {
            // If not followed yet
            if (follows.size() == 0) {
                checkForMutualInterests(user);
                checkForMutualFriends(user);
                suggestIfFollowYou(user);
            }
        });
    }

    // Suggest user if they have followed current user - not followed back
    private void suggestIfFollowYou(User user) {
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_FROM, user);
        query.whereEqualTo(Follow.KEY_TO, mCurrentUser);

        query.findInBackground((followList, e) -> {
            // if other user has followed current user
            if (followList.size() == 1) {
                if (!mSuggestedUsersList.contains(user)) {
                    user.setSuggestionReason("Follows you");
                    mSuggestedUsersList.add(user);
                    mAdapter.notifyItemInserted(mSuggestedUsersList.size() - 1);
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    // For each user, get a list of their interests.
    // Compare their interests list with current user's interests list.
    // If there are mutual interests between You and Random user, suggest that user
    @SuppressLint("DefaultLocale")
    private void checkForMutualInterests(User friend) {
        final List<Category> userInterests = new ArrayList<>();
        final List<Category> friendInterests = new ArrayList<>();

        // query current user's interests
        ParseQuery<Category> query = mCurrentUser.getInterests().getQuery();
        query.findInBackground((interests, e) -> {
            userInterests.addAll(interests);

            // query friend's interests
            ParseQuery<Category> queryFriend = friend.getInterests().getQuery();
            queryFriend.findInBackground((interestsList, e1) -> {
                friendInterests.addAll(interestsList);

                // create a list of common interests from 2 lists above
                List<Category> common = new ArrayList(friendInterests);
                common.retainAll(userInterests);

                // if there's any common interests, add user to suggestions list
                int commonInterestsCount = common.size();
                if (commonInterestsCount > 0) {
                    if (!mSuggestedUsersList.contains(friend)) {
                        Category commonInterest = common.get(0);   // first common
                        if (commonInterestsCount == 1) {
                            friend.setSuggestionReason(String.format("You both like %s",
                                    commonInterest.getCategoryName()));
                        } else {
                            friend.setSuggestionReason(String.format("You both like %s + %d more",
                                    commonInterest.getCategoryName(), commonInterestsCount - 1));
                        }

                        // add user to suggestions list
                        mSuggestedUsersList.add(friend);
                        mAdapter.notifyItemInserted(mSuggestedUsersList.size() - 1);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        });
    }


    // For each user not followed by current user -> get a list of their followers.
    // Compare their followers list with current user's following list.
    // If there are mutual friends between You and Random user, suggest that user
    @SuppressLint("DefaultLocale")
    private void checkForMutualFriends(User user) {

        // create 2 lists: current user's followings AND other user's followers
        List<User> otherUserFollowers = new ArrayList<>();
        List<User> currentUserFollowings = new ArrayList<>();

        // query other user's followers. add to list
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        query.whereEqualTo(Follow.KEY_TO, user);
        query.include(Follow.KEY_FROM);
        query.findInBackground((otherFollowList, e) -> {
            for (Follow follow : otherFollowList) {
                otherUserFollowers.add(follow.getFrom());
            }

            // query current user's following list. add to list.
            ParseQuery<Follow> query1 = ParseQuery.getQuery(Follow.class);

            query1.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
            query1.include(Follow.KEY_TO);
            query1.findInBackground((currentFollowList, e1) -> {
                for (Follow follow : currentFollowList) {
                    currentUserFollowings.add(follow.getTo());
                }

                // create a list of common followers between current user & other user
                List<User> common = new ArrayList(otherUserFollowers);
                common.retainAll(currentUserFollowings);

                // if there's any mutual followers, add user to suggestions list
                int commonFollowersCount = common.size();
                if (commonFollowersCount > 0) {
                    if (!mSuggestedUsersList.contains(user)) {
                        User commonFollower = common.get(0);
                        if (commonFollowersCount == 1) {
                            user.setSuggestionReason(String.format("Followed by %s",
                                    commonFollower.getName()));
                        } else {
                            user.setSuggestionReason(String.format("Followed by %s + %d more",
                                    commonFollower.getName(), commonFollowersCount - 1));
                        }

                        // add user to suggestions list
                        mSuggestedUsersList.add(user);
                        mAdapter.notifyItemInserted(mSuggestedUsersList.size() - 1);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        });
    }



}