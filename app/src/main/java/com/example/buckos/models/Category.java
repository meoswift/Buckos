package com.example.buckos.models;

import androidx.annotation.NonNull;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

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
}
