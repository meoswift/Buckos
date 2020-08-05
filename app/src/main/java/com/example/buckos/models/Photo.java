package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

// This class represents a Media object in the Parse database
@ParseClassName("Media")
@Parcel(analyze = {Photo.class})
public class Photo extends ParseObject {
    public static final String KEY_IMAGE_FILE = "image";
    public static final String KEY_ITEM = "item";
    public static final String KEY_LIST = "list";
    public static final String KEY_AUTHOR = "author";


    public ParseFile getPhotoFile() {
        return getParseFile(KEY_IMAGE_FILE);
    }

    public void setPhotoFile(ParseFile file) {
        put(KEY_IMAGE_FILE, file);
    }

    public Item getItem() {
        return (Item) get(KEY_ITEM);
    }

    public void setItem(Item item) {
        put(KEY_ITEM, item);
    }

    public BucketList getList() {
        return (BucketList) getParseObject(KEY_LIST);
    }

    public void setList(BucketList list) {
        put(KEY_LIST, list);
    }

    public void setAuthor(User user) {
        put(KEY_AUTHOR, user);
    }

    public User getAuthor() {
        return (User) getParseUser(KEY_AUTHOR);
    }
}