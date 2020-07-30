package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@ParseClassName("Like")
@Parcel(analyze = {Like.class})
public class Like extends ParseObject {
    public static final String KEY_FROM_USER = "fromUser";
    public static final String KEY_TO_STORY = "toStory";

    public User getLikeFromUser() {
        return (User) getParseUser(KEY_FROM_USER);
    }

    public void setLikeFromUser(User user) {
        put(KEY_FROM_USER, user);
    }

    public Story getLikeToStory() {
        return (Story) getParseObject(KEY_TO_STORY);
    }

    public void setLikeToStory(Story story) {
        put(KEY_TO_STORY, story);
    }
}
