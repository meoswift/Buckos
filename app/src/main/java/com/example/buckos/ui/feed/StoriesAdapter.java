package com.example.buckos.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
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
import com.example.buckos.ui.buckets.userprofile.ProfileFragment;
import com.example.buckos.ui.create.NewItemActivity;
import com.example.buckos.ui.explore.othersprofile.OthersProfileFragment;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

// Adapter that inflates a Story item and displays that in RecyclerView
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private List<Story> mStoriesList;
    private Context mContext;
    private Fragment mFragment;
    private User mCurrentUser;

    public StoriesAdapter(List<Story> storiesList, Context context, Fragment fragment) {
        mStoriesList = storiesList;
        mContext = context;
        mFragment = fragment;
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

        // set profile pic of author
        holder.setProfilePic(author, holder.authorProfilePicImageView);
        // set profile pic of current user
        holder.setProfilePic(mCurrentUser, holder.userProfilePic);
        // Open user's profile on profile pic clicked
        holder.userProfilePic.setOnClickListener(v -> {
            openProfile(mCurrentUser);
        });
        holder.authorProfilePicImageView.setOnClickListener(v -> {
            openProfile(author);
        });

        // set heart button - a post is liked or not
        setLikeButtonOnLikeStatus(holder.heartButton, story);

        // get the comments and the likes count
        getCommentsCount(holder.commentsCountTextView, story);
        getLikesCount(holder.likesCountTextView, story);

        // display the photos in a story
        holder.setAdapterForPhotos(story);

    }

    private void openProfile(User author) {
        // Open selected user's profile
        Fragment fragment;

        if (author.equals(mCurrentUser)) {
            fragment = new ProfileFragment();
        } else {
            Bundle bundle = new Bundle();
            fragment = new OthersProfileFragment();
            bundle.putParcelable("user", Parcels.wrap(author));
            fragment.setArguments(bundle);
        }

        mFragment.getParentFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, 0, R.anim.enter_from_left, 0)
                .replace(R.id.your_placeholder, fragment)
                .addToBackStack(null)
                .commit();
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
        private TextView isCompletedTextView;
        private ImageButton heartButton;
        private ImageView userProfilePic;
        private ImageButton storyMoreMenu;

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
            userProfilePic = itemView.findViewById(R.id.userProfilePic);
            isCompletedTextView = itemView.findViewById(R.id.isCompletedLabel);
            storyMoreMenu = itemView.findViewById(R.id.storyMoreMenu);

            TextView newCommentBox = itemView.findViewById(R.id.newCommentBox);

            // handle liking
            heartButton = itemView.findViewById(R.id.heartButton);
            heartButton.setOnClickListener(this);

            // handle commenting
            ImageButton commentIcon = itemView.findViewById(R.id.commentIcon);
            LinearLayout commentBox = itemView.findViewById(R.id.commentBox);
            commentBox.setOnClickListener(this);
            newCommentBox.setOnClickListener(this);
            commentIcon.setOnClickListener(this);

            // handle More menu
            storyMoreMenu.setOnClickListener(this);
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

        public void setAdapterForPhotos(Story story) {
            PhotosAdapter mPhotosAdapter = new PhotosAdapter(story.getPhotosInStory(), mContext);
            storyPhotosRecyclerView.setAdapter(mPhotosAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            storyPhotosRecyclerView.setLayoutManager(layoutManager);
        }

        @Override
        public void onClick(View v) {
            Story story = mStoriesList.get(getAdapterPosition());

            switch (v.getId()) {
                case R.id.commentBox:
                case R.id.commentIcon:
                case R.id.newCommentBox:
                    Intent intent = new Intent(mContext, CommentsActivity.class);
                    intent.putExtra("story", Parcels.wrap(story));
                    mContext.startActivity(intent);
                    break;
                case R.id.heartButton:
                    story = mStoriesList.get(getAdapterPosition());
                    toggleLikeStory(story);
                    break;
                case R.id.storyMoreMenu:
                    handleStoryMenuClicked();
                    break;
            }
        }

        private void handleStoryMenuClicked() {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(mContext, storyMoreMenu);
            //inflating menu from xml resource
            popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());
            popup.show();

            // adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_save) {
                    saveItemToList(getAdapterPosition());
                }
                return false;
            });
        }

        // On click, either like or unlike the story. update database accordingly.
        private void toggleLikeStory(final Story story) {
            ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
            query.whereEqualTo(Like.KEY_TO_STORY, story);
            query.whereEqualTo(Like.KEY_FROM_USER, mCurrentUser);
            query.findInBackground((likes, e) -> {
                // user haven't liked story
                if (likes.size() == 0) {
                    likeStory(story);
                } else {
                    unlikeStory(story);
                }
            });

        }

        private void likeStory(Story story) {
            int currentLikeCount = Integer.parseInt(likesCountTextView.getText().toString());

            Like like = new Like();
            like.setLikeFromUser(mCurrentUser);
            like.setLikeToStory(story);
            like.saveInBackground();
            likesCountTextView.setText(String.valueOf(currentLikeCount + 1));
            updateLikeButtonOnLikeStatus(heartButton, true);
        }

        private void unlikeStory(final Story story) {
            final int currentLikeCount = Integer.parseInt(likesCountTextView.getText().toString());

            ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
            query.whereEqualTo(Like.KEY_FROM_USER, mCurrentUser);
            query.whereEqualTo(Like.KEY_TO_STORY, story);
            query.findInBackground((likeList, e) -> {
                Like like = likeList.get(0);
                like.deleteInBackground();
                likesCountTextView.setText(String.valueOf(currentLikeCount - 1));
                updateLikeButtonOnLikeStatus(heartButton,false);
            });

        }
    }

    // query whether an user has liked a button or not. set the drawable for like button
    // based on like status
    private void setLikeButtonOnLikeStatus(final ImageButton heartButton, Story story) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_TO_STORY, story);
        query.whereEqualTo(Like.KEY_FROM_USER, mCurrentUser);
        query.findInBackground((likes, e) -> {
            // user haven't liked story
            if (likes.size() == 0) {
                updateLikeButtonOnLikeStatus(heartButton, false);
            } else {
                updateLikeButtonOnLikeStatus(heartButton, true);
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
        query.countInBackground((count, e) -> {
            commentsCount.setText(String.valueOf(count));
        });
    }

    // query for total number of likes on a Story
    private void getLikesCount(final TextView likesCount, Story story) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_TO_STORY, story);
        query.countInBackground((count, e) -> {
            likesCount.setText(String.valueOf(count));
        });
    }

    private void saveItemToList(int position) {
        Story story = mStoriesList.get(position);
        Intent intent = new Intent(mContext, NewItemActivity.class);
        intent.putExtra("title", story.getTitle());
        mContext.startActivity(intent);
    }



}
