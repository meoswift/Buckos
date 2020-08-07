package com.example.buckos.ui.explore.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.buckos.R;
import com.example.buckos.models.Category;
import com.example.buckos.ui.buckets.items.DoneFragment;
import com.example.buckos.ui.buckets.items.InProgressFragment;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

// Fragment that displays the suggestions for each category - includes stories or trending items
public class CategoryDetailsFragment extends Fragment {

    private Category mCategory;
    private TabLayout mTabLayout;
    private Fragment mFragment;
    private Bundle mBundle;

    public CategoryDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton backButton = view.findViewById(R.id.backButton);
        mTabLayout = view.findViewById(R.id.categoryTabLayout);

        // get the selected category
        mBundle = getArguments();
        mCategory = Parcels.unwrap(mBundle.getParcelable("category"));

        // set color and title
        setUpCategoryToolbar(view);

        // when first opened, display Trending by default
        inflateDefaultTab();
        // update tab - trending or stories - on selection
        updateTabOnSelected();

        backButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }

    private void inflateDefaultTab() {
        mFragment = new TrendingFragment(); // Default tab
        mFragment.setArguments(mBundle);
        updateTabWithFragment();
    }

    private void setUpCategoryToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.categoryToolbar);
        TextView categoryToolbarTitle = view.findViewById(R.id.categoryToolbarTitle);
        getActivity().getWindow().setStatusBarColor(mCategory.getBackgroundColor());

        toolbar.setBackgroundColor(mCategory.getBackgroundColor());
        categoryToolbarTitle.setText(mCategory.getName());
    }

    // When using swipes between two tabs, update the according fragment
    public void updateTabOnSelected() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                // Open the fragment depending on which tab was chosen
                switch (position) {
                    case 0:
                        mFragment = new TrendingFragment();
                        mFragment.setArguments(mBundle);
                        break;
                    case 1:
                        mFragment = new StoriesCategoryFragment();
                        mFragment.setArguments(mBundle);
                        break;
                }

                updateTabWithFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Function to replace layout with the Tab selected
    private void updateTabWithFragment() {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.tab_placeholder, mFragment)
                .addToBackStack(null)
                .commit();
    }

}