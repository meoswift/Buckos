package com.example.buckos.ui.explore.category;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Category;

import org.parceler.Parcels;

import java.util.List;

// Adapter that get information about list category and display to RecyclerView
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private Context mContext;
    private List<Category> mCategories;
    private Fragment mFragment;

    public CategoriesAdapter(Context context, Fragment fragment, List<Category> categories) {
        mContext = context;
        mFragment = fragment;
        mCategories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.categoryNameTextView.setText(category.getName());
        holder.categoryLayout.setBackgroundColor(category.getBackgroundColor());
        Glide.with(mContext).load(category.getCategoryIcon()).into(holder.categoryIconImageView);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout categoryLayout;
        private TextView categoryNameTextView;
        private ImageView categoryIconImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryLayout = itemView.findViewById(R.id.categoryLayout);
            categoryNameTextView = itemView.findViewById(R.id.categoryName);
            categoryIconImageView = itemView.findViewById(R.id.categoryIcon);

            itemView.setOnClickListener(v -> {
                queryStoriesOfSelectedCategory();
            });
        }

        // For each category, get a list of stories posted by users from all over the app
        private void queryStoriesOfSelectedCategory() {
            Category category = mCategories.get(getAdapterPosition());

            // specify which category
            Bundle bundle = new Bundle();
            bundle.putParcelable("category", Parcels.wrap(category));

            // opens fragment with stories of this category
            Fragment fragment = new CategoryDetailsFragment();
            fragment.setArguments(bundle);
            mFragment.getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, 0, R.anim.enter_from_left, 0)
                    .replace(R.id.your_placeholder, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
