package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Item")
@Parcel(analyze={Item.class})
public class Item extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_COMPLETED = "completed";
    public static final String KEY_LIST = "list";
    public static final String KEY_CREATED_AT = "createdAt";

    public String getName() {
        return getString(KEY_NAME);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public Boolean getCompleted() {
        return getBoolean(KEY_COMPLETED);
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
}
