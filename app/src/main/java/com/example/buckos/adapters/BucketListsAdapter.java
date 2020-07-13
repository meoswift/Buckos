package com.example.buckos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;

import java.util.List;

public class BucketListsAdapter extends RecyclerView.Adapter<BucketListsAdapter.ViewHolder> {

    List<BucketList> mBucketLists;
    Context context;

    public BucketListsAdapter(Context context, List<BucketList> mBucketLists) {
        this.mBucketLists = mBucketLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BucketList list = mBucketLists.get(position);
        holder.mListTitleTv.setText(list.getName());
        holder.mListDescriptionTv.setText(list.getDescription());
    }

    @Override
    public int getItemCount() {
        return mBucketLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mListTitleTv;
        TextView mListDescriptionTv;


        public ViewHolder(@NonNull View item) {
            super(item);

            mListTitleTv = item.findViewById(R.id.titleListTv);
            mListDescriptionTv = item.findViewById(R.id.descriptionListTv);
        }
    }
}
