package com.example.buckos.ui.create;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.example.buckos.ui.buckets.items.itemdetails.PhotosAdapter;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

// Adapter to inflate a Story suggestion view and display in RecyclerView
public class StorySuggestionsAdapter extends RecyclerView.Adapter<StorySuggestionsAdapter.ViewHolder> {

    private Context mContext;
    private List<Item> mStorySuggestions;
    private List<Photo> mPhotosInSuggestion;
    private int checkedPosition = 0; // check the first story by default


    public StorySuggestionsAdapter(Context context, List<Item> storySuggestions) {
        mContext = context;
        mStorySuggestions = storySuggestions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.story_suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item story = mStorySuggestions.get(position);
        User author = (User) ParseUser.getCurrentUser();
        BucketList list = story.getList();

        // set all information on a post
        holder.authorDisplayNameTextView.setText(author.getName());
        holder.storyTitleTextView.setText(story.getName());
        holder.storyDescriptionTextView.setText(story.getDescription());
        holder.listTitleTextView.setText(list.getName());
        holder.selectedButton.setChecked(checkedPosition == position);

        holder.setProfilePic(author);

        holder.setAdapterForPhotos(story);
    }

    @Override
    public int getItemCount() {
        return mStorySuggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView authorProfilePicImageView;
        private TextView authorDisplayNameTextView;
        private TextView storyTitleTextView;
        private TextView storyDescriptionTextView;
        private TextView listTitleTextView;
        private RecyclerView storyPhotosRecyclerView;
        private RadioButton selectedButton;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            authorProfilePicImageView = itemView.findViewById(R.id.authorProfilePic);
            authorDisplayNameTextView = itemView.findViewById(R.id.authorDisplayName);
            storyTitleTextView = itemView.findViewById(R.id.storyTitle);
            storyDescriptionTextView = itemView.findViewById(R.id.storyDescription);
            storyPhotosRecyclerView = itemView.findViewById(R.id.storyPhotosRv);
            listTitleTextView = itemView.findViewById(R.id.listTitleTv);
            selectedButton = itemView.findViewById(R.id.selectedButton);

            itemView.setOnClickListener(this);
        }

        // Set profile pic with either file from database or default image
        private void setProfilePic(User user) {
            ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

            if (image != null) {
                Glide.with(mContext).load(image.getUrl()).circleCrop()
                        .into(authorProfilePicImageView);
            } else {
                Glide.with(mContext).load(R.drawable.bucket).circleCrop()
                        .into(authorProfilePicImageView);
            }
        }

        // PhotosAdapter for photos in each story suggestions
        public void setAdapterForPhotos(Item story) {
            mPhotosInSuggestion = new ArrayList<>();

            PhotosAdapter mPhotosAdapter = new PhotosAdapter(mPhotosInSuggestion, mContext);
            storyPhotosRecyclerView.setAdapter(mPhotosAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            storyPhotosRecyclerView.setLayoutManager(layoutManager);

            mPhotosAdapter.displayPhotosInCurrentItem(story);
        }

        @Override
        public void onClick(View v) {
            // single-selection
            int position = getAdapterPosition();
            if (checkedPosition != position) {
                notifyItemChanged(position);
                notifyItemChanged(checkedPosition);
                checkedPosition = position;
            }
        }
    }

    // get the selected story to post to feed
    public Item getSelectedStory() {
        if (checkedPosition != -1) {
            return mStorySuggestions.get(checkedPosition);
        }

        return null;
    }

}
