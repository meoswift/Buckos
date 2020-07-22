package com.example.buckos.ui.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Story;
import com.example.buckos.ui.buckets.items.itemdetails.PhotosAdapter;
import com.example.buckos.ui.buckets.userprofile.User;
import com.parse.ParseFile;

import java.util.List;

// Adapter that inflates a Story item and displays that in RecyclerView
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private List<Story> mStoriesList;
    private Context mContext;

    public StoriesAdapter(List<Story> storiesList, Context context) {
        this.mStoriesList = storiesList;
        this.mContext = context;
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

        holder.authorDisplayNameTextView.setText(author.getName());
        holder.storyTitleTextView.setText(story.getTitle());
        holder.storyDescriptionTextView.setText(story.getDescription());
        holder.storyTimeStamp.setText(story.getFormatedTime());
        holder.listTitleTextView.setText(list.getName());
        holder.setProfilePic(author);

        holder.setAdapterForPhotos(story);
    }

    @Override
    public int getItemCount() {
        return mStoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView authorProfilePicImageView;
        private TextView authorDisplayNameTextView;
        private TextView storyTitleTextView;
        private TextView storyDescriptionTextView;
        private TextView storyTimeStamp;
        private TextView listTitleTextView;

        private RecyclerView storyPhotosRecyclerView;
        private PhotosAdapter mPhotosAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            authorProfilePicImageView = itemView.findViewById(R.id.authorProfilePic);
            authorDisplayNameTextView = itemView.findViewById(R.id.authorDisplayName);
            storyTitleTextView = itemView.findViewById(R.id.storyTitle);
            storyDescriptionTextView = itemView.findViewById(R.id.storyDescription);
            storyPhotosRecyclerView = itemView.findViewById(R.id.storyPhotosRv);
            storyTimeStamp = itemView.findViewById(R.id.storyTimeStamp);
            listTitleTextView = itemView.findViewById(R.id.listTitleTv);
        }

        // Set profile pic with either file from database or default image
        private void setProfilePic(User user) {
            ParseFile image = (ParseFile) user.get(User.KEY_PROFILE_PIC);

            if (image != null)
                Glide.with(mContext).load(image.getUrl()).circleCrop().into(authorProfilePicImageView);
            else
                Glide.with(mContext).load(R.drawable.bucket).circleCrop().into(authorProfilePicImageView);
        }

        public void setAdapterForPhotos(Story story) {
            mPhotosAdapter = new PhotosAdapter(story.getPhotosInStory(), mContext);
            storyPhotosRecyclerView.setAdapter(mPhotosAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext,
                    LinearLayoutManager.HORIZONTAL, false);
            storyPhotosRecyclerView.setLayoutManager(layoutManager);
        }
    }

}