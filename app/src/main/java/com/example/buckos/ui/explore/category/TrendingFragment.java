package com.example.buckos.ui.explore.category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.buckos.R;
import com.example.buckos.models.Category;
import com.example.buckos.models.Story;
import com.example.buckos.models.Suggestion;
import com.example.buckos.ui.feed.StoriesAdapter;

import org.parceler.Parcels;

import java.util.List;

public class TrendingFragment extends Fragment {

    private Category mCategory;
    private List<Suggestion> mSuggestions;
    private SuggestionsAdapter mAdapter;
    private RecyclerView mTrendingRecyclerView;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trending, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // find views
        mTrendingRecyclerView = view.findViewById(R.id.trendingRv);

        // get the selected category
        Bundle bundle = getArguments();
        mCategory = Parcels.unwrap(bundle.getParcelable("category"));

        displayTrendingItems();
    }

    private void displayTrendingItems() {
        switch (mCategory.getName()) {
            case "Travel":
            case "Just for fun":
            case "Education":
            case "Sports":
                mSuggestions = Suggestion.getTravelSuggestions();
                break;
            case "Food & Drinks":
                mSuggestions = Suggestion.getFoodSuggestions();
                break;
            case "TV & Movies":
                mSuggestions = Suggestion.getMovieSuggestions();
                break;

        }
        mAdapter = new SuggestionsAdapter(getContext(), mSuggestions);
        mTrendingRecyclerView.setAdapter(mAdapter);
        mTrendingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}