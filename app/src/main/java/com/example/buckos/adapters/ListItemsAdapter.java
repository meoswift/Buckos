package com.example.buckos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Item;

import java.util.List;

public class ListItemsAdapter extends RecyclerView.Adapter<ListItemsAdapter.ViewHolder> {

    private List<Item> mItemList;
    private Context mContext;

    public ListItemsAdapter(Context context, List<Item> mItemList) {
        this.mItemList = mItemList;
        this.mContext = context;
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mItemTitleTv;
        TextView mItemNoteTv;
        ImageView mCheckBoxIv;

        public ViewHolder(@NonNull View item) {
            super(item);

            mItemTitleTv = item.findViewById(R.id.titleItemTv);
            mItemNoteTv = item.findViewById(R.id.noteItemTv);
            mCheckBoxIv = item.findViewById(R.id.checkBoxIv);

        }
    }
}
