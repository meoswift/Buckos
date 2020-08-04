package com.example.buckos.ui.feed;

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
import com.example.buckos.models.Category;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.example.buckos.ui.buckets.userprofile.ProfileFragment;
import com.example.buckos.ui.explore.OthersProfileFragment;
import com.example.buckos.ui.explore.UsersAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

// This adapter bind views of a Follow suggestion item in Welcome layout in Home Fragment
public class FollowSuggestionsAdapter extends RecyclerView.Adapter<FollowSuggestionsAdapter.ViewHolder> {

    private List<User> mFollowSuggestions;
    private Context mContext;
    private Fragment mFragment;
    private User mCurrentUser;

    public FollowSuggestionsAdapter(List<User> followSuggestions, Context context,
                                    Fragment fragment) {
        mFollowSuggestions = followSuggestions;
        mContext = context;
        mFragment = fragment;
        mCurrentUser = (User) ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public FollowSuggestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.follow_suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mFollowSuggestions.get(position);
        ParseRelation<Category> interests = user.getInterests();
        ParseQuery<Category> query = interests.getQuery();
        query.getFirstInBackground((interest, e) -> {
            setProfilePic(user, holder.profilePicImageView);
            holder.displayNameTextView.setText(user.getName());
            holder.usernameTextView.setText(String.format("@%s", user.getUsername()));
            holder.bioTextView.setText(user.getBio());
            holder.interestsTextView.setText(String.format("Interested in %s",
                    interest.getCategoryName()));
        });

    }

    // Set profile pic with either file from database or default image
    private void setProfilePic(User user, ImageView profilePicHolder) {
        ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

        if (image != null) {
            Glide.with(mContext).load(image.getUrl()).circleCrop()
                    .into(profilePicHolder);
        } else {
            Glide.with(mContext).load(R.drawable.no_profile_pic).circleCrop()
                    .into(profilePicHolder);
        }
    }

    @Override
    public int getItemCount() {
        return mFollowSuggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePicImageView;
        private TextView displayNameTextView;
        private TextView usernameTextView;
        private TextView bioTextView;
        private TextView interestsTextView;
        private Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicImageView = itemView.findViewById(R.id.userProfilePic);
            displayNameTextView = itemView.findViewById(R.id.userDisplayName);
            usernameTextView = itemView.findViewById(R.id.userUsername);
            bioTextView = itemView.findViewById(R.id.userBio);
            interestsTextView = itemView.findViewById(R.id.userInterests);
            followButton = itemView.findViewById(R.id.followButton);

            followButton.setOnClickListener(v -> {
                modifyFollowingStatusOnClick(followButton, getAdapterPosition());
            });

            itemView.setOnClickListener(v -> {
                openProfile(getAdapterPosition());
            });
        }
    }

    private void openProfile(int position) {
        // Open selected user's profile
        User user = mFollowSuggestions.get(position);
        Fragment fragment;

        if (user.equals(mCurrentUser)) {
            fragment = new ProfileFragment();
        } else {
            Bundle bundle = new Bundle();
            fragment = new OthersProfileFragment();
            bundle.putParcelable("user", Parcels.wrap(user));
            fragment.setArguments(bundle);
        }

        mFragment.getParentFragmentManager().beginTransaction()
                .replace(R.id.your_placeholder, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void modifyFollowingStatusOnClick(final Button followButton, int position) {
        final User selectedUser = mFollowSuggestions.get(position);
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);
        ParseRelation<User> friends = mCurrentUser.getFriends();

        // modify follow button based on whether current user has followed selected user
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.whereEqualTo(Follow.KEY_TO, selectedUser);
        query.findInBackground((followList, e) -> {
            // have not followed
            if (followList.size() == 0) {
                Follow relationship = new Follow();
                relationship.setFrom(mCurrentUser);
                relationship.setTo(selectedUser);
                relationship.saveInBackground();
                friends.add(selectedUser);
                mCurrentUser.saveInBackground();
                setFollowingButton(followButton);
            } else {
                Follow relationship = followList.get(0);
                relationship.deleteInBackground();
                friends.remove(selectedUser);
                mCurrentUser.saveInBackground();
                setFollowButton(followButton);
            }
        });
    }

    private void setFollowButton(Button followButton) {
        followButton.setText("Follow");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
    }

    private void setFollowingButton(Button followButton) {
        followButton.setText("Following");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
    }


}
