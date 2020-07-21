package com.example.buckos.main.feed;

import com.example.buckos.main.buckets.BucketList;
import com.example.buckos.main.buckets.items.Item;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

// This class represents a Story object in the Parse database
@ParseClassName("Story")
@Parcel(analyze={Story.class})
public class Story extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_ITEM = "item";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public ParseObject getItem() {
        return (Item) getParseObject(KEY_ITEM);
    }

    public void setTitle(String name) {
        put(KEY_TITLE, name);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setItem(Item item) {
        put(KEY_ITEM, item);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public String getShortenedDescription() {
        String description = getDescription();
        if (description.length() > 200) {
            return description.substring(0, 200) + "...";
        } else {
            return description;
        }
    }
}