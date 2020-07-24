package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

// This class represents a Item object in the Parse database
@ParseClassName("Item")
@Parcel(analyze = {Item.class})
public class Item extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_LIST = "list";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_TIME_COMPLETED = "timeCompleted";

    public String getName() {
        return getString(KEY_NAME);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public BucketList getList() {
        return (BucketList) getParseObject(KEY_LIST);
    }

    public Boolean getCompleted() {
        return getBoolean(KEY_COMPLETED);
    }

    public void setTimeCompleted(Date completed) {
        put(KEY_TIME_COMPLETED, completed);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public void setCompleted(Boolean completed) {
        put(KEY_COMPLETED, completed);
    }

    public void setList(BucketList list) {
        put(KEY_LIST, list);
    }

    public void setAuthor(ParseUser user) {
        put(KEY_AUTHOR, user);
    }

    public String getShortenedDescription() {
        String description = getDescription();
        if (description.length() > 40) {
            return description.substring(0, 40) + "...";
        } else {
            return description;
        }
    }



}
