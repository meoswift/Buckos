package com.example.buckos.app.buckets;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.app.buckets.items.Item;
import com.example.buckos.app.buckets.items.ListDetailsActivity;
import com.example.buckos.app.buckets.items.content.Photo;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
        holder.listTitleTv.setText(list.getName());

        if (!list.getDescription().equals("")) {
            holder.listDescriptionTv.setText(list.getDescription());
            holder.listDescriptionTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mBucketLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView listTitleTv;
        TextView listDescriptionTv;

        public ViewHolder(@NonNull View item) {
            super(item);

            listTitleTv = item.findViewById(R.id.listTitle);
            listDescriptionTv = item.findViewById(R.id.listDescription);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayListDetails();
                }
            });
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
                deleteItemsInList(list);
                mBucketLists.remove(position);
                notifyItemRemoved(position);
                Snackbar snackbar = Snackbar.make(mView, "List deleted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    // When user delete a list, delete all items in that list as well
    private void deleteItemsInList(BucketList list) {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.whereEqualTo(Item.KEY_LIST, list);
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    final Item item = objects.get(i);
                    item.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            deleteAllPhotosInItem(item);
                        }
                    });
                }
            }
        });
    }

    // When user delete an item, delete all photos in that item
    public void deleteAllPhotosInItem(Item item) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Photo.KEY_ITEM, item);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    Photo photo = objects.get(i);
                    photo.deleteInBackground();
                }
            }
        });
    }

}
