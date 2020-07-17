package com.example.buckos.main.profile.bucketlists.items.content;

import android.provider.MediaStore;

import com.example.buckos.main.profile.bucketlists.items.Item;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.parceler.Parcel;

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