package com.example.buckos.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.Objects;

@ParseClassName("Category")
@Parcel(analyze = {Category.class})
public class Category extends ParseObject {
    public static final String KEY_NAME = "name";

    public String getCategoryName() {
        return getString(KEY_NAME);
    }

    @NonNull
    @Override
    public String toString() {
        return getCategoryName();
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
