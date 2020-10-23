package com.example.buckos.ui.travel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.buckos.R;
import com.example.buckos.models.City;
import com.example.buckos.ui.travel.city.TravelFragment;

import java.util.List;

// Fragment that display top cities to visit
public class TravelExploreFragment extends Fragment {

    public TravelExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_travel_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set the status bar color to white after changing
        getActivity().getWindow().setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));

        // switch to search layout
        ImageButton searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            Fragment fragment = new TravelFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.your_placeholder, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        List<City> cities = City.getCities();
        RecyclerView topCitiesRecyclerView = view.findViewById(R.id.topCitiesRv);
        CitiesAdapter adapter = new CitiesAdapter(cities, this);
        topCitiesRecyclerView.setAdapter(adapter);
        topCitiesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }


}