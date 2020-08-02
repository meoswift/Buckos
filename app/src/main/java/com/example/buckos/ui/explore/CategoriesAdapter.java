package com.example.buckos.ui.explore;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.models.Category;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private Context mContext;
    private List<Category> mCategories;

    public CategoriesAdapter(Context context, List<Category> categories) {
        this.mContext = context;
        this.mCategories = categories;
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
        }
    }
}
