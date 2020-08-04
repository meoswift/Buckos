package com.example.buckos.models;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.buckos.R;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// This object represents the category of a list, item, story, and user's interests. There are
// 6 categories that users can choose from.
@ParseClassName("Category")
@Parcel(analyze = {Category.class})
public class Category extends ParseObject {
    public static final String KEY_NAME = "name";

    private String mName;
    private int mCategoryIcon;
    private int mBackgroundColor;

    public Category() {};

    // Initialize category for Explore tab
    public Category(String name, int categoryIcon, String color) {
        mName = name;
        mCategoryIcon = categoryIcon;
        mBackgroundColor = Color.parseColor(color);
    }

    // Get categories from Parse database
    public String getCategoryName() {
        return getString(KEY_NAME);
    }

    @NonNull
    @Override
    public String toString() {
        return getCategoryName();
    }

    public String getName() {
        return mName;
    }

    public int getCategoryIcon() {
        return mCategoryIcon;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    // Returns a list of contacts
    public static List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Travel", R.drawable.travel, "#e76f51"));          // blue
        categories.add(new Category("Food & Drinks", R.drawable.food, "#F6AE2D"));   // red
        categories.add(new Category("Just for fun", R.drawable.jff, "#86BBD8"));    // orange
        categories.add(new Category("TV & Movies",R.drawable.movies, "#33658A"));     // yellow
        categories.add(new Category("Education", R.drawable.education, "#43AA8B"));       // purple
        categories.add(new Category("Sports", R.drawable.sports, "#2F4858"));          // green

        return categories;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Category category = (Category) obj;
        return Objects.equals(getObjectId(), category.getObjectId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId());
    }
}
