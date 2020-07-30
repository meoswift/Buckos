package com.example.buckos.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Category;
import com.example.buckos.models.Comment;
import com.example.buckos.models.Like;
import com.example.buckos.models.Story;
import com.example.buckos.ui.buckets.items.itemdetails.PhotosAdapter;
import com.example.buckos.models.User;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

// Adapter that inflates a Story item and displays that in RecyclerView
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private List<Story> mStoriesList;
    private Context mContext;
    private User mCurrentUser;

    public StoriesAdapter(List<Story> storiesList, Context context) {
        this.mStoriesList = storiesList;
        this.mContext = context;
        mCurrentUser = (User) ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = mStoriesList.get(position);
        User author = (User) story.getAuthor();
        BucketList list = story.getBucketList();
        Category category = story.getCategory();

        holder.authorDisplayNameTextView.setText(author.getName());
        holder.storyTitleTextView.setText(story.getTitle());
        holder.storyDescriptionTextView.setText(story.getDescription());
        holder.storyTimeStamp.setText(story.getFormatedTime());
        holder.listTitleTextView.setText(list.getName());
        holder.categoryTagTextView.setText(category.getCategoryName());
        holder.setProfilePic(author);
        setLikeButtonOnLikeStatus(holder.heartButton, story);

        getCommentsCount(holder.commentsCountTextView, story);
        getLikesCount(holder.likesCountTextView, story);

        holder.setAdapterForPhotos(story);
    }

    @Override
    public int getItemCount() {
        return mStoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView commentsCountTextView;
        private TextView likesCountTextView;
        private ImageView authorProfilePicImageView;
        private TextView authorDisplayNameTextView;
        private TextView storyTitleTextView;
        private TextView storyDescriptionTextView;
        private TextView storyTimeStamp;
        private TextView listTitleTextView;
        private TextView categoryTagTextView;
        private ImageButton heartButton;

        private RecyclerView storyPhotosRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            authorProfilePicImageView = itemView.findViewById(R.id.authorProfilePic);
            authorDisplayNameTextView = itemView.findViewById(R.id.authorDisplayName);
            storyTitleTextView = itemView.findViewById(R.id.storyTitle);
            storyDescriptionTextView = itemView.findViewById(R.id.storyDescription);
            storyPhotosRecyclerView = itemView.findViewById(R.id.storyPhotosRv);
            storyTimeStamp = itemView.findViewById(R.id.storyTimeStamp);
            listTitleTextView = itemView.findViewById(R.id.listTitleTv);
            categoryTagTextView = itemView.findViewById(R.id.categoryTag);
            commentsCountTextView = itemView.findViewById(R.id.commentCountTv);
            likesCountTextView = itemView.findViewById(R.id.heartCountTv);

            // handle liking
            heartButton = itemView.findViewById(R.id.heartButton);
            heartButton.setOnClickListener(this);

            // handle commenting
            ImageButton commentIcon = itemView.findViewById(R.id.commentIcon);
            LinearLayout commentBox = itemView.findViewById(R.id.commentBox);

            commentBox.setOnClickListener(this);
            commentIcon.setOnClickListener(this);
        }

        // Set profile pic with either file from database or default image
        private void setProfilePic(User user) {
            ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).circleCrop()
                        .into(authorProfilePicImageView);
            } else {
                Glide.with(mContext).load(R.drawable.no_profile_pic).circleCrop()
                        .into(authorProfilePicImageView);
            }
        }

        public void setAdapterForPhotos(Story story) {
            PhotosAdapter mPhotosAdapter = new PhotosAdapter(story.getPhotosInStory(), mContext);
            storyPhotosRecyclerView.setAdapter(mPhotosAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            storyPhotosRecyclerView.setLayoutManager(layoutManager);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.commentBox:
                case R.id.commentIcon:
                    Story story = mStoriesList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("story", Parcels.wrap(story));
                    mContext.startActivity(intent);
                    break;
                case R.id.heartButton:
                    story = mStoriesList.get(getAdapterPosition());
                    toggleLikeStory(story);
                    break;
            }
        }

        // On click, either like or unlike the story. update database accordingly.
        private void toggleLikeStory(final Story story) {
            ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
            query.whereEqualTo(Like.KEY_FROM_USER, mCurrentUser);
            query.whereEqualTo(Like.KEY_TO_STORY, story);
            query.findInBackground(new FindCallback<Like>() {
                @Override
                public void done(List<Like> likes, ParseException e) {
                    int currentLikeCount = Integer.parseInt(likesCountTextView.getText().toString());
                    // not liked yet -> like post and update like count
                    if (likes.size() == 0) {
                        Like like = new Like();
                        like.setLikeFromUser(mCurrentUser);
                        like.setLikeToStory(story);
                        like.saveInBackground();
                        likesCountTextView.setText(String.valueOf(currentLikeCount + 1));
                        updateLikeButtonOnLikeStatus(heartButton, true);
                    // liked already -> unlike post and update like count
                    } else {
                        Like like = likes.get(0);
                        like.deleteInBackground();
                        likesCountTextView.setText(String.valueOf(currentLikeCount - 1));
                        updateLikeButtonOnLikeStatus(heartButton,false);
                    }
                }
            });
        }
    }

    // query whether an user has liked a button or not. set the drawable for like button
    // based on like status
    private void setLikeButtonOnLikeStatus(final ImageButton heartButton, Story story) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_FROM_USER, mCurrentUser);
        query.whereEqualTo(Like.KEY_TO_STORY, story);
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                if (likes.size() == 0) {
                    updateLikeButtonOnLikeStatus(heartButton, false);
                } else {
                    updateLikeButtonOnLikeStatus(heartButton, true);
                }
            }
        });
    }

    // when user like or unlike a post, update the like button drawable
    private void updateLikeButtonOnLikeStatus(ImageButton heartButton, boolean isLiked) {
        if (isLiked) {
            Drawable res = mContext.getDrawable(R.drawable.ic_baseline_favorite_24);
            heartButton.setImageDrawable(res);
        } else {
            Drawable res = mContext.getDrawable(R.drawable.ic_baseline_favorite_border_24);
            heartButton.setImageDrawable(res);
        }
    }

    // query for total number of comments on a Story
    private void getCommentsCount(final TextView commentsCount, Story story) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        // include the user object related to the story
        query.include(Comment.KEY_AUTHOR);
        query.whereEqualTo(Comment.KEY_STORY, story);
        query.orderByAscending(Comment.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                commentsCount.setText(String.valueOf(comments.size()));
            }
        });
    }

    // query for total number of likes on a Story
    private void getLikesCount(final TextView likesCount, Story story) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_TO_STORY, story);
        // start an asynchronous call
        query.findInBackground(new FindCallback<Like>() {
            public void done(List<Like> likes, ParseException e) {
                likesCount.setText(String.valueOf(likes.size()));
            }
        });
    }


}
