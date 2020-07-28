package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

// This class represents a List object in the Parse database
@ParseClassName("List")
@Parcel(analyze = {BucketList.class})
public class BucketList extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_CATEGORY = "category";

    private boolean isSelected = false;

    public String getName() {
        return getString(KEY_NAME);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public Category getCategory() {
        return (Category) getParseObject(KEY_CATEGORY);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setCategory(Category category) {
        put(KEY_CATEGORY, category);
    }

    public String getShortenedTitle() {
        String description = getName();
        if (description.length() > 25) {
            return description.substring(0, 25) + "...";
        } else {
            return description;
        }
    }
}
