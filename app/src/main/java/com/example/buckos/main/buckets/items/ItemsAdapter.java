package com.example.buckos.main.buckets.items;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.main.buckets.items.InProgressFragment;
import com.example.buckos.main.buckets.items.Item;
import com.example.buckos.main.buckets.items.content.ItemDetailsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

// Adapter that inflates an Item object into View and display that Item in RecyclerView
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<Item> mItemList;
    private Context mContext;
    private Fragment mActivity;

    public ItemsAdapter(Context context, List<Item> itemList, Fragment activity) {
        mItemList = itemList;
        mContext = context;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsAdapter.ViewHolder holder, int position) {
        Item item = mItemList.get(position);
        holder.itemTitleTv.setText(item.getName());

        // When there is no note in an item, remove that view
        if (!item.getShortenedDescription().equals("")) {
            holder.itemNoteTv.setText(item.getShortenedDescription());
            holder.itemNoteTv.setVisibility(View.VISIBLE);
        } else {
            holder.itemNoteTv.setVisibility(View.GONE);
        }

        // For Done items, there will be no checkbox and image preview
        if (item.getCompleted()) {
            holder.checkBoxIv.setImageDrawable(null);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemTitleTv;
        TextView itemNoteTv;
        ImageView checkBoxIv;
        ImageView photoPreview;

        Item item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views
            itemTitleTv = itemView.findViewById(R.id.itemTitle);
            itemNoteTv = itemView.findViewById(R.id.itemDescription);
            checkBoxIv = itemView.findViewById(R.id.checkBox);

            // Directs the user to details view of an item
            itemView.setOnClickListener(this);

            // Mark item completed when user check the box
            checkBoxIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkItemCompleted();
                }
            });
        }

        // Show details of an Item. User can also edit the note.
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            item = mItemList.get(position);
            Intent intent = new Intent(mContext, ItemDetailsActivity.class);
            intent.putExtra("item", Parcels.wrap(item));
            intent.putExtra("position", position);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) mContext, itemTitleTv, "itemTitle");
            mActivity.startActivityForResult(intent, InProgressFragment.MODIFY_ITEM_REQ, options.toBundle());
        }

        // If an item is completed, update in database and remove from view
        // pop up a Snackbar that allows user to Undo in case they accidentally
        private void checkItemCompleted() {
            int position = getAdapterPosition();
            item = mItemList.get(position);
            item.setCompleted(true);
            item.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Drawable res = mContext.getResources().getDrawable(R.drawable.ic_check_box);
                    checkBoxIv.setImageDrawable(res);
                    mItemList.remove(item);
                    notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(itemView, "1 item archived to Done", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onUndoClicked(item);
                                }
                            }).show();
                }
            });
        }

        // Update database and display item back into view
        private void onUndoClicked(final Item item) {
            item.setCompleted(false);
            item.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Drawable res = mContext.getResources().getDrawable(R.drawable.ic_check_box_outline);
                    checkBoxIv.setImageDrawable(res);
                    mItemList.add(0, item);
                    notifyItemInserted(0);
                }
            });
        }
    }

}
