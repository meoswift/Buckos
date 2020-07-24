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
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
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
        mFollowingUsers = mCurrentUser.getFollowingUsers();
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
        Glide.with(mContext)
                .load(mContext.getDrawable(R.drawable.bucket))
                .circleCrop()
                .into(holder.profilePicImageView);

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

        ParseQuery query = mFollowingUsers.getQuery();

        // if selected user is in following list, set button to Following status
        query.whereEqualTo(User.KEY_OBJECT_ID, selectedUser.getObjectId());
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> followingList, ParseException e) {
                if (followingList.size() == 1) {
                    setFollowingButton(holder.followButton);
                }
            }
        });
    }

    private void modifyFollowingStatusOnClick(final Button followButton, int position) {
        final User selectedUser = mUsersResults.get(position);

        ParseQuery query = mFollowingUsers.getQuery();

        // if selected user is in following list, set button to Following status
        query.whereEqualTo(User.KEY_OBJECT_ID, selectedUser.getObjectId());
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> followingList, ParseException e) {
                if (followingList.size() == 1) {
                    mFollowingUsers.remove(selectedUser);
                    setFollowButton(followButton);
                } else {
                    mFollowingUsers.add(selectedUser);
                    setFollowingButton(followButton);
                }

                mCurrentUser.saveInBackground();
            }
        });
    }
}
