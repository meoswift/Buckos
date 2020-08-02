package com.example.buckos.ui.explore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.Category;

import java.util.List;

// This fragment shows categories that user can discover and also allow user to search for
// other users when clicking on Search icon
public class ExploreFragment extends Fragment {

    private CategoriesAdapter mAdapter;

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView categoriesRecyclerView = view.findViewById(R.id.categoriesRv);
        TextView searchBoxTextView = view.findViewById(R.id.searchBox);

        // allows for optimizations in RecyclerView
        categoriesRecyclerView.setHasFixedSize(true);

        // Define 2 column grid layout - display 6 categories of bucket lists
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mAdapter = new CategoriesAdapter(getContext(), Category.getCategories());
        categoriesRecyclerView.setAdapter(mAdapter);

        // when user click on search box, directs user to search fragment
        searchBoxTextView.setOnClickListener(v -> {
            Fragment fragment = new SearchUserFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.your_placeholder, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }
}