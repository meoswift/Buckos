package com.example.buckos.models;

import android.text.format.DateUtils;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public static final String KEY_LIST = "list";
    public static final String KEY_CATEGORY = "category";

    private List<Photo> mPhotosInStory;

    public Story() {
        mPhotosInStory = new ArrayList<>();
    }

    public List<Photo> getPhotosInStory() {
        return mPhotosInStory;
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

    public Item getItem() {
        return (Item) getParseObject(KEY_ITEM);
    }

    public BucketList getBucketList() {
        return (BucketList) getParseObject(KEY_LIST);
    }

    public Category getCategory() {
        return (Category) getParseObject(KEY_CATEGORY);
    }

    public void setPhotosInStory(List<Photo> photosInStory) {
        mPhotosInStory = photosInStory;
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

    public void setList(BucketList list) {
        put(KEY_LIST, list);
    }

    public void setCategory(Category category) {
        put(KEY_CATEGORY, category);
    }

    public String getFormatedTime() {
        long dateMillis = getCreatedAt().getTime();
        String ago = DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();

        return ago;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Story story = (Story) o;
        return Objects.equals(getObjectId(), story.getObjectId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId());
    }

    public void deleteCommentsInStory(Story story) {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.whereEqualTo(Comment.KEY_STORY, story);
        query.findInBackground((comments, e) -> {
            for (Comment comment : comments) {
                comment.deleteInBackground();
            }
        });
    }

    public void deleteLikesInStory(Story story) {
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo(Like.KEY_TO_STORY, story);
        query.findInBackground((likes, e) -> {
            for (Like like : likes) {
                like.deleteInBackground();
            }
        });
    }
}