package com.example.buckos.ui.travel.city.placebookmark;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;

import java.util.ArrayList;
import java.util.List;

// This adapter inflates a Bucket List item and displays that in the RecyclerView.
public class TravelListsAdapter extends RecyclerView.Adapter<TravelListsAdapter.ViewHolder> {

    private List<BucketList> mTravelLists;
    private Context mContext;
    private List<BucketList> mSelectedLists;

    public TravelListsAdapter(List<BucketList> travelLists, Context context) {
        mTravelLists = travelLists;
        mContext = context;
        mSelectedLists = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.travel_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BucketList list = mTravelLists.get(position);
        holder.travelListTitle.setText(list.getName());

        if (!list.getDescription().equals("")) {
            holder.travelListDescription.setVisibility(View.VISIBLE);
            holder.travelListDescription.setText(list.getDescription());
        } else {
            holder.travelListDescription.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mTravelLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView travelListTitle;
        private TextView travelListDescription;
        private ImageView checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            travelListTitle = itemView.findViewById(R.id.listTitle);
            travelListDescription = itemView.findViewById(R.id.listDescription);
            checkBox = itemView.findViewById(R.id.checkBox);

            final Drawable notSelected = mContext.getDrawable(R.drawable.ic_check_box_outline);
            final Drawable selected = mContext.getDrawable(R.drawable.ic_check_box);

            // When user click on a check box, set Travel List to selected
            // Add that Travel List to the list of selected items
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BucketList list = mTravelLists.get(getAdapterPosition());
                    list.setSelected(!list.isSelected());

                    if (list.isSelected()) {
                        checkBox.setImageDrawable(selected);
                        mSelectedLists.add(list);
                    } else {
                        checkBox.setImageDrawable(notSelected);
                        mSelectedLists.remove(list);
                    }
                }
            });
        }
    }

    // Get the list of selected travel lists on RecyclerView
    public List<BucketList> getSelectedLists() {
        return mSelectedLists;
    }
}
