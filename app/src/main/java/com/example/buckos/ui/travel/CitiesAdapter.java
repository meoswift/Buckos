package com.example.buckos.ui.travel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.City;

import java.util.List;

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

        holder.cityCard.setOnClickListener(v -> {
            Fragment fragment = new TravelFragment();
            getParentFragmentManager().beginTransaction()
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cityNameTextView = itemView.findViewById(R.id.cityName);
            countryNameTextView = itemView.findViewById(R.id.countryName);
            cityCard = itemView.findViewById(R.id.cityCard);
        }
    }
}