package com.example.buckos.ui.travel;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Place;
import com.example.buckos.ui.travel.placebookmark.SaveToListActivity;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

// Adapter that inflates a Place item and displays that in the RecyclerView
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Place> mPlaces;
    private Context mContext;
    private Fragment mFragment;

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
    public void onBindViewHolder(@NonNull final PlacesAdapter.ViewHolder holder, final int position) {
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

        // Bookmark place - User can add to multiple lists or add to a new one
        private void savePlaceToList() {
            Place place = mPlaces.get(getAdapterPosition());
            place.setBookmarked(!place.isBookmarked());

            final Drawable bookmarked = mContext.getDrawable(R.drawable.baseline_bookmark);
            if (place.isBookmarked()) {
                bookmarkButton.setImageDrawable(bookmarked);
                Item item = new Item();
                item.setName(place.getName());
                item.setCompleted(false);
                item.setAuthor(ParseUser.getCurrentUser());
                item.setDescription(place.getAddressName());

                Intent intent = new Intent(mContext, SaveToListActivity.class);
                intent.putExtra("item", Parcels.wrap(item));
                mFragment.startActivityForResult(intent, SAVE_TO_LIST);
            } else {
                Toast.makeText(mContext, "Place already saved!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
