package com.example.buckos.ui.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Follow;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

// Adapter that inflates one User item to view and display in RecyclerView
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUsersResults;
    private Context mContext;
    private ParseRelation<User> mFollowingUsers;
    private User mCurrentUser;

    public UsersAdapter(List<User> usersResults, Context context) {
        mUsersResults = usersResults;
        mContext = context;
        mCurrentUser = (User) ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsersResults.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.displayNameTextView.setText(user.getName());
        holder.setProfilePic(user);

        // if the result is current user, follow button cannot show
        if (user.getObjectId().equals(mCurrentUser.getObjectId())) {
            holder.followButton.setVisibility(View.GONE);
        }

        setFollowButton(holder.followButton);
        setSelectedUserFollowingStatus(holder, position);
    }



    @Override
    public int getItemCount() {
        return mUsersResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView profilePicImageView;
        private TextView usernameTextView;
        private TextView displayNameTextView;
        private Button followButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicImageView = itemView.findViewById(R.id.authorProfilePic);
            usernameTextView = itemView.findViewById(R.id.nameTv);
            displayNameTextView = itemView.findViewById(R.id.displayNameTv);
            followButton = itemView.findViewById(R.id.followButton);

            followButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Add selected user to current user's list of following
            if (v.getId() == R.id.followButton)
                modifyFollowingStatusOnClick(followButton, getAdapterPosition());
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
    }

    private void setFollowButton(Button followButton) {
        followButton.setText("Follow");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
    }

    private void setFollowingButton(Button followButton) {
        followButton.setText("Following");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
    }

    private void setSelectedUserFollowingStatus(final ViewHolder holder, int position) {
        final User selectedUser = mUsersResults.get(position);
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);

        // if selected user is followed by current user, button becomes Following
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.whereEqualTo(Follow.KEY_TO, selectedUser);

        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                if (followList.size() == 1) {
                    setFollowingButton(holder.followButton);
                }
            }
        });

    }

    private void modifyFollowingStatusOnClick(final Button followButton, int position) {
        final User selectedUser = mUsersResults.get(position);
        ParseQuery<Follow> query = ParseQuery.getQuery(Follow.class);

        // modify follow button based on whether current user has followed selected user
        query.whereEqualTo(Follow.KEY_FROM, mCurrentUser);
        query.whereEqualTo(Follow.KEY_TO, selectedUser);
        query.findInBackground(new FindCallback<Follow>() {
            @Override
            public void done(List<Follow> followList, ParseException e) {
                // have not followed
                if (followList.size() == 0) {
                    Follow relationship = new Follow();
                    relationship.setFrom(mCurrentUser);
                    relationship.setTo(selectedUser);
                    relationship.saveInBackground();
                    setFollowingButton(followButton);
                } else {
                    Follow relationship = followList.get(0);
                    relationship.deleteInBackground();
                    setFollowButton(followButton);
                }
            }
        });
    }

}
