package com.example.buckos.main_screen.travel_explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private Context context;

    public PlacesAdapter(List<Place> places, Context context) {
        this.mPlaces = places;
        this.context = context;
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
        holder.mPlaceNameTv.setText(place.getName());
        holder.mPlaceAddressTv.setText(place.getAddressName());
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlaceNameTv;
        private TextView mPlaceAddressTv;
        private RatingBar mRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mPlaceNameTv = itemView.findViewById(R.id.placeNameTv);
            mPlaceAddressTv = itemView.findViewById(R.id.placeAddressTv);
        }
    }
}
