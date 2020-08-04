package com.example.buckos.ui.buckets;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Category;
import com.example.buckos.models.Item;
import com.example.buckos.models.Story;
import com.example.buckos.models.User;
import com.example.buckos.ui.buckets.items.ListDetailsActivity;
import com.example.buckos.models.Photo;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

// Adapter to inflate a BucketList object into View and display in RecyclerView
public class BucketListsAdapter extends RecyclerView.Adapter<BucketListsAdapter.ViewHolder> {

    List<BucketList> mBucketLists;
    Context mContext;
    View mView;
    Fragment mFragment;

    public BucketListsAdapter(Context context, List<BucketList> bucketLists,
                              View view, Fragment fragment) {
        mBucketLists = bucketLists;
        mContext = context;
        mView = view;
        mFragment = fragment;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bucket_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BucketList list = mBucketLists.get(position);
        Category category = list.getCategory();
        holder.listTitleTextView.setText(list.getName());

        if (!list.getDescription().equals("")) {
            holder.listDescriptionTextView.setText(list.getDescription());
            holder.listDescriptionTextView.setVisibility(View.VISIBLE);
        }

        holder.categoryTagTextView.setText(category.getCategoryName());
        holder.bucketListCardView.setOnClickListener(v -> {
            holder.displayListDetails();
        });
    }

    @Override
    public int getItemCount() {
        return mBucketLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTitleTextView;
        TextView listDescriptionTextView;
        TextView categoryTagTextView;
        CardView bucketListCardView;

        public ViewHolder(@NonNull View item) {
            super(item);

            listTitleTextView = item.findViewById(R.id.listTitle);
            listDescriptionTextView = item.findViewById(R.id.listDescription);
            categoryTagTextView = item.findViewById(R.id.categoryTag);
            bucketListCardView = item.findViewById(R.id.listCard);
        }

        private void displayListDetails() {
            int position = getAdapterPosition();
            BucketList list = mBucketLists.get(position);

            // When user click on a specific list, show the details of the list
            Intent intent = new Intent(mContext, ListDetailsActivity.class);
            intent.putExtra("bucketList", Parcels.wrap(list));

            // request used when user click Post on an item in list
            mFragment.startActivityForResult(intent, BucketsFragment.POST_ITEM_REQUEST);
        }
    }

    // Delete the list when user swipe right or left
    public void deleteList(final int position) {
        final BucketList list = mBucketLists.get(position);
        list.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {

                // when deleting  a list, we have to delete
                // items in list, photos in list, and stories in list
                deleteItemsInList(list);
                deleteAllPhotosInList(list);
                deleteAllStoriesInList(list);
                deleteInterestIfNeeded(list);
                mBucketLists.remove(position);
                notifyItemRemoved(position);
                Snackbar snackbar = Snackbar.make(mView, "List deleted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void deleteInterestIfNeeded(final BucketList list) {
        final User user = (User) ParseUser.getCurrentUser();

        ParseQuery<BucketList> query = ParseQuery.getQuery(BucketList.class);
        query.whereEqualTo(BucketList.KEY_CATEGORY, list.getCategory());
        query.whereEqualTo(BucketList.KEY_AUTHOR, user);
        query.include(BucketList.KEY_CATEGORY);
        query.findInBackground(new FindCallback<BucketList>() {
            @Override
            public void done(List<BucketList> lists, ParseException e) {
                Category interest = list.getCategory();

                // if there's no longer any list of this category, delete interest
                if (lists.size() == 0) {
                    ParseRelation<Category> interests = user.getInterests();
                    interests.remove(interest);
                    user.saveInBackground();
                }
            }
        });
    }

    // When user delete a list, delete all items in that list as well
    private void deleteItemsInList(BucketList list) {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.whereEqualTo(Item.KEY_LIST, list);
        query.findInBackground((objects, e) -> {
            for (int i = 0; i < objects.size(); i++) {
                final Item item = objects.get(i);
                item.deleteInBackground();
            }
        });
    }

    // When user delete an item, delete all photos in that item
    private void deleteAllPhotosInList(BucketList list) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Photo.KEY_LIST, list);
        query.findInBackground((photos, e) -> {
            for (int i = 0; i < photos.size(); i++) {
                Photo photo = photos.get(i);
                photo.deleteInBackground();
            }
        });
    }

    private void deleteAllStoriesInList(BucketList list) {
        ParseQuery<Story> query = ParseQuery.getQuery(Story.class);
        query.whereEqualTo(Photo.KEY_LIST, list);
        query.findInBackground((stories, e) -> {
            for (int i = 0; i < stories.size(); i++) {
                Story story = stories.get(i);
                story.deleteCommentsInStory(story);
                story.deleteLikesInStory(story);
                story.deleteInBackground();
            }
        });
    }



}
