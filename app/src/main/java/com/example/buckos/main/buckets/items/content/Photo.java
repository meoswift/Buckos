package com.example.buckos.main.buckets.items.content;

import com.example.buckos.main.buckets.items.Item;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

// This class represents a Media object in the Parse database
@ParseClassName("Media")
@Parcel(analyze={Photo.class})
public class Photo extends ParseObject {
    public static final String KEY_IMAGE_FILE = "image";
    public static final String KEY_ITEM = "item";

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
}