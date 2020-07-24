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

public class SuggestedUsersAdapter extends RecyclerView.Adapter<SuggestedUsersAdapter.ViewHolder> {

    private List<User> mSuggestedUsers;
    private Context mContext;

    public SuggestedUsersAdapter(List<User> suggestedUsers, Context context) {
        mSuggestedUsers = suggestedUsers;
        mContext = context;
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
        holder.followedByTextView.setText(user.getFollowedBy());
        Glide.with(mContext)
                .load(mContext.getDrawable(R.drawable.bucket))
                .circleCrop()
                .into(holder.profilePicImageView);

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
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            modifyFollowingStatusOnClick(followButton, position);
        }
    }

    private void modifyFollowingStatusOnClick(final Button followButton, int position) {
        final User selectedUser = mSuggestedUsers.get(position);
        final User currentUser = (User) ParseUser.getCurrentUser();

        final ParseRelation<User> followingList = currentUser.getFollowingUsers();
        ParseQuery query = followingList.getQuery();

        // if selected user is in following list, set button to Following status
        query.whereEqualTo(User.KEY_OBJECT_ID, selectedUser.getObjectId());
        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> followings, ParseException e) {
                if (followings.size() == 1) {
                    followingList.remove(selectedUser);
                    setFollowButton(followButton);
                } else {
                    followingList.add(selectedUser);
                    setFollowingButton(followButton);
                }

                currentUser.saveInBackground();
            }
        });
    }


    private void setFollowingButton(Button followButton) {
        followButton.setText("Following");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
    }

    private void setFollowButton(Button followButton) {
        followButton.setText("Follow");
        followButton.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        followButton.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorWhite));
    }


}
