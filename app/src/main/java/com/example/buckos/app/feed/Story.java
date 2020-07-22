package com.example.buckos.app.feed;

import android.text.format.DateUtils;

import com.example.buckos.app.buckets.items.Item;
import com.example.buckos.app.buckets.items.content.Photo;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;

// This class represents a Story object in the Parse database. Each Story object has a list of
// Photo objects.
@ParseClassName("Story")
@Parcel(analyze = {Story.class})
public class Story extends ParseObject {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_ITEM = "item";
    public static final String KEY_CREATED_AT = "createdAt";
    private List<Photo> mPhotosInStory;

    public Story() {
    }

    public List<Photo> getPhotosInStory() {
        return mPhotosInStory;
    }

    public void setPhotosInStory(List<Photo> photosInStory) {
        mPhotosInStory = photosInStory;
    }

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

    public String getFormatedTime() {
        long dateMillis = getCreatedAt().getTime();
        String ago = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return ago;
    }
}