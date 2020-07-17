package com.example.buckos.main_screen.travel_explore.list_of_places_in_city;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.main_screen.travel_explore.save_place_to_bucket_list.SaveToListActivity;
import com.example.buckos.main_screen.user_profile.user_bucket_lists.bucket_list_items.Item;

import org.parceler.Parcels;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private Context mContext;

    public PlacesAdapter(List<Place> places, Context context) {
        mPlaces = places;
        mContext = context;
    }

    @NonNull
    @Override
    public PlacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create an inflater that inflates all views in a Place item
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View place = inflater.inflate(R.layout.place_item, parent, false);
        return new ViewHolder(place);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.ViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.placeNameTv.setText(place.getName());
        holder.placeAddressTv.setText(place.getAddressName());
        holder.ratingBar.setRating(place.getRating());
        holder.userRating.setText(String.valueOf(place.getRating()));
        holder.userRatingCount.setText(place.getUserRatingsTotal());
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView placeNameTv;
        private TextView placeAddressTv;
        private RatingBar ratingBar;
        private TextView userRating;
        private TextView userRatingCount;
        private ImageView bookmarkButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            placeNameTv = itemView.findViewById(R.id.placeNameTv);
            placeAddressTv = itemView.findViewById(R.id.placeAddressTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            userRating = itemView.findViewById(R.id.userRating);
            userRatingCount = itemView.findViewById(R.id.userRatingCount);
            bookmarkButton = itemView.findViewById(R.id.bookmarkButton);

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savePlaceToList();
                }
            });
        }

        private void savePlaceToList() {
            Place place = mPlaces.get(getAdapterPosition());
            Intent intent = new Intent(mContext, SaveToListActivity.class);
            intent.putExtra("place", Parcels.wrap(place));
            mContext.startActivity(intent);
        }
    }

}