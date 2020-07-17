package com.example.buckos.main.profile.bucketlists;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.main.profile.bucketlists.items.Item;
import com.example.buckos.main.profile.bucketlists.items.ListDetailsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.List;

// This adapter inflates a BucketList object into View and display in RecyclerView
public class BucketListsAdapter extends RecyclerView.Adapter<BucketListsAdapter.ViewHolder> {

    List<BucketList> mBucketLists;
    Context mContext;
    View mView;

    public BucketListsAdapter(Context context, List<BucketList> bucketLists, View view) {
        mBucketLists = bucketLists;
        mContext = context;
        mView = view;
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

        if(!list.getDescription().equals("")) {
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
            mContext.startActivity(intent);
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
                    Item item = objects.get(i);
                    item.deleteInBackground();
                }
            }
        });
    }

}