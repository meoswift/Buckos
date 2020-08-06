package com.example.buckos.ui.explore.category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private StoriesAdapter mAdapter;
    private ProgressBar mStoriesCategoryProgressBar;

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
        mStoriesCategoryProgressBar = view.findViewById(R.id.storiesCategoryProgressBar);

        // get the selected category
        Bundle bundle = getArguments();
        mCategory = Parcels.unwrap(bundle.getParcelable("category"));
    }
}