package com.example.buckos.ui.explore.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.example.buckos.ui.buckets.userprofile.ProfileFragment;
import com.example.buckos.ui.explore.othersprofile.OthersProfileFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

// Adapter that gets information of a suggested user to follow and display to RecyclerView
public class SuggestedUsersAdapter extends RecyclerView.Adapter<SuggestedUsersAdapter.ViewHolder> {

    private List<User> mSuggestedUsers;
    private Context mContext;
    private Fragment mFragment;

    public SuggestedUsersAdapter(List<User> suggestedUsers, Context context, Fragment fragment) {
        mSuggestedUsers = suggestedUsers;
        mContext = context;
        mFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.suggested_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mSuggestedUsers.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.displayNameTextView.setText(user.getName());
        holder.followedByTextView.setText(user.getSuggestionReason());
        holder.setProfilePic(user);

        setFollowButton(holder.followButton);
    }

    @Override
    public int getItemCount() {
        return mSuggestedUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView profilePicImageView;
        private TextView usernameTextView;
        private TextView displayNameTextView;
        private Button followButton;
        private TextView followedByTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicImageView = itemView.findViewById(R.id.authorProfilePic);
            usernameTextView = itemView.findViewById(R.id.nameTv);
            displayNameTextView = itemView.findViewById(R.id.displayNameTv);
            followButton = itemView.findViewById(R.id.followButton);
            followedByTextView = itemView.findViewById(R.id.followedByTv);

            followButton.setOnClickListener(this);
            itemView.setOnClickListener(v -> {
                openProfile();
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == R.id.followButton) {
                modifyFollowingStatusOnClick(followButton, position);
            }
        }

        // Set profile pic with either file from database or default image
        private void setProfilePic(User user) {
            ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

            if (image != null)
                Glide.with(mContext).load(image.getUrl()).circleCrop().into(profilePicImageView);
            else
                Glide.with(mContext).load(R.drawable.no_profile_pic)
                        .circleCrop().into(profilePicImageView);
        }

        private void openProfile() {
            // Open selected user's profile
            User user = mSuggestedUsers.get(getAdapterPosition());
            User currentUser = (User) ParseUser.getCurrentUser();
            Fragment fragment;

            if (user.equals(currentUser)) {
                fragment = new ProfileFragment();
            } else {
                Bundle bundle = new Bundle();
                fragment = new OthersProfileFragment();
                bundle.putParcelable("user", Parcels.wrap(user));
                fragment.setArguments(bundle);
            }

            mFragment.getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, 0, R.anim.enter_from_left, 0)
                    .replace(R.id.your_placeholder, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }


    private void modifyFollowingStatusOnClick(final Button followButton, int position) {
        final User selectedUser = mSuggestedUsers.get(position);
        final User currentUser = (User) ParseUser.getCurrentUser();
        ParseRelation<User> friends = currentUser.getFriends();
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);


        // modify follow button based on whether current user has followed selected user
        query.whereEqualTo(Follow.KEY_FROM, currentUser);
        query.whereEqualTo(Follow.KEY_TO, selectedUser);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                // have not followed
                if (followList.size() == 0) {
                    Follow relationship = new Follow();
                    relationship.setFrom(currentUser);
                    relationship.setTo(selectedUser);
                    relationship.saveInBackground();
                    friends.add(selectedUser);
                    currentUser.saveInBackground();
                    setFollowingButton(followButton);
                } else {
                    Follow relationship = followList.get(0);
                    relationship.deleteInBackground();
                    friends.remove(selectedUser);
                    currentUser.saveInBackground();
                    setFollowButton(followButton);
                }
            }
        });
    }


    private void setFollowingButton(Button followButton) {
        followButton.setText("Following");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
    }

    private void setFollowButton(Button followButton) {
        followButton.setText("Follow");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
    }

}
