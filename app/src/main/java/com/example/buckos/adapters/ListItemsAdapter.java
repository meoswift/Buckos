package com.example.buckos.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.activities.ItemDetailsActivity;
import com.example.buckos.fragments.InProgressFragment;
import com.example.buckos.models.Item;
import com.google.android.material.snackbar.Snackbar;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ViewHolder> {

    private List<Item> mItemList;
    private Context mContext;
    private Activity mActivity;

    public ListItemsAdapter(Context context, List<Item> itemList, Activity activity) {
        this.mItemList = itemList;
        this.mContext = context;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ListItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemsAdapter.ViewHolder holder, int position) {
        Item item = mItemList.get(position);
        holder.mItemTitleTv.setText(item.getName());
        holder.mItemNoteTv.setText(item.getDescription());

        // For Done items, there will be no checkbox
        if (item.getCompleted())
            holder.mCheckBoxIv.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mItemTitleTv;
        TextView mItemNoteTv;
        ImageView mCheckBoxIv;
        Item item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views
            mItemTitleTv = itemView.findViewById(R.id.titleItemTv);
            mItemNoteTv = itemView.findViewById(R.id.noteItemTv);
            mCheckBoxIv = itemView.findViewById(R.id.checkBoxIv);

            // Directs the user to details view of an item
            itemView.setOnClickListener(this);

            // Mark item completed when user check the box
            mCheckBoxIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkItemCompleted();
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            item = mItemList.get(position);
            Intent intent = new Intent(mContext, ItemDetailsActivity.class);
            intent.putExtra("item", Parcels.wrap(item));
            mActivity.startActivityForResult(intent, InProgressFragment.EDIT_ITEM_REQ);
        }

        private void checkItemCompleted() {
            int position = getAdapterPosition();
            item = mItemList.get(position);
            item.setCompleted(true);
            item.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Drawable res = mContext.getResources().getDrawable(R.drawable.ic_baseline_check_box_24);
                    mCheckBoxIv.setImageDrawable(res);
                    mItemList.remove(item);
                    notifyItemRemoved(getAdapterPosition());
                    Snackbar.make(itemView, "1 item archived to Done", Snackbar.LENGTH_LONG)
                            .setAction("Action", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onUndoClicked(item);
                                }
                            }).show();
                }
            });
        }

        private void onUndoClicked(final Item item) {
            item.setCompleted(false);
            item.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Drawable res = mContext.getResources().getDrawable(R.drawable.ic_baseline_check_box_outline_blank_24);
                    mCheckBoxIv.setImageDrawable(res);
                    mItemList.add(0, item);
                    notifyItemInserted(0);
                }
            });
        }
    }
}
