package com.example.buckos.ui.buckets.items;

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
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Story;
import com.example.buckos.ui.buckets.items.itemdetails.ItemDetailsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

// Adapter that inflates an Item object into View and display that Item in RecyclerView
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public static final String KEY_ITEM = "item";
    public static final String KEY_POSITION = "postition";

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
        holder.itemTitleTextView.setText(item.getName());

        // When there is no note in an item, remove that view
        if (!item.getShortenedDescription().equals("")) {
            holder.itemNoteTextView.setText(item.getShortenedDescription());
            holder.itemNoteTextView.setVisibility(View.VISIBLE);
        } else {
            holder.itemNoteTextView.setVisibility(View.GONE);
        }

        // For Done items, there will be no checkbox and image preview
        if (item.getCompleted()) {
            holder.checkBoxImageView.setImageDrawable(null);
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView itemTitleTextView;
        TextView itemNoteTextView;
        ImageView checkBoxImageView;
        ImageView photoPreview;

        Item item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views
            itemTitleTextView = itemView.findViewById(R.id.itemTitle);
            itemNoteTextView = itemView.findViewById(R.id.itemDescription);
            checkBoxImageView = itemView.findViewById(R.id.checkBox);

            // Directs the user to details view of an item
            itemView.setOnClickListener(this);

            // Mark item completed when user check the box
            checkBoxImageView.setOnClickListener(new View.OnClickListener() {
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
            intent.putExtra(KEY_ITEM, Parcels.wrap(item));
            intent.putExtra(KEY_POSITION, position);

            // create shared elements pairs and animation
            Pair titles = Pair.create((View) itemTitleTextView, "itemTitle");
            Pair notes = Pair.create((View) itemNoteTextView, "itemNote");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) mContext, titles, notes);

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
                    checkBoxImageView.setImageDrawable(res);
                    mItemList.remove(item);
                    notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(itemView, R.string.item_mark_done, Snackbar.LENGTH_LONG)
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
                    checkBoxImageView.setImageDrawable(res);
                    mItemList.add(0, item);
                    notifyItemInserted(0);
                }
            });
        }
    }

}
