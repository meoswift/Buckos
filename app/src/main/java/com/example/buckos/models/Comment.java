package com.example.buckos.models;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// This class represents a Comment object in the Parse database
@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_BODY = "body";
    public static final String KEY_AUTHOR = "user";
    public static final String KEY_STORY = "story";
    public static final String KEY_CREATED_AT = "createdAt";

    public Comment() {}

    public String getBody() {
        return getString(KEY_BODY);
    }

    public void setBody(String body) {
        put(KEY_BODY, body);
    }

    public User getUser() {
        return (User) getParseUser(KEY_AUTHOR);
    }

    public void setUser(User user) {
        put(KEY_AUTHOR, user);
    }

    public Story getStory() {
        return (Story) getParseObject(KEY_STORY);
    }

    public void setStory(Story story) {
        put(KEY_STORY, story);
    }
}