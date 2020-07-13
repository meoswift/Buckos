package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("List")
@Parcel(analyze={BucketList.class})
public class BucketList extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_ITEMS = "items";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIST_ID = "objectId";

    public String getName() {
        return getString(KEY_NAME);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public ParseRelation<Item> getItems() {
        return getRelation(KEY_ITEMS);
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
}
