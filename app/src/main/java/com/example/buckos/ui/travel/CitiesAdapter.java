package com.example.buckos.ui.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.City;
import com.example.buckos.ui.travel.city.TravelFragment;

import java.util.List;

// Adapter that gets information about a top city and display that to RecyclerView
public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.ViewHolder> {

    List<City> mCities;
    Fragment mFragment;

    public CitiesAdapter(List<City> cities, Fragment fragment) {
        mCities = cities;
        mFragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.city_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = mCities.get(position);

        holder.cityNameTextView.setText(city.getCityName());
        holder.countryNameTextView.setText(city.getCountry());
        Glide.with(mFragment)
                .load(city.getCityImage())
                .centerCrop()
                .placeholder(R.drawable.image_background).into(holder.cityImageView);

        holder.cityCard.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("query", city.getCityName());

            // use city name clicked on as query for search fragment
            Fragment fragment = new TravelFragment();
            fragment.setArguments(bundle);
            mFragment.getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, 0, R.anim.enter_from_left, 0)
                    .replace(R.id.your_placeholder, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityNameTextView;
        private TextView countryNameTextView;
        private CardView cityCard;
        private ImageView cityImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cityNameTextView = itemView.findViewById(R.id.cityName);
            countryNameTextView = itemView.findViewById(R.id.countryName);
            cityImageView = itemView.findViewById(R.id.cityImage);
            cityCard = itemView.findViewById(R.id.cityCard);
        }
    }
}
