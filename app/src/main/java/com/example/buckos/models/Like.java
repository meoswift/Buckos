package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

// This Like object represent the like relationship between an User and a Story. When an user
// like a post, a Like object is created.
@ParseClassName("Like")
@Parcel(analyze = {Like.class})
public class Like extends ParseObject {
    public static final String KEY_FROM_USER = "fromUser";
    public static final String KEY_TO_STORY = "toStory";

    public void setLikeFromUser(User user) {
        put(KEY_FROM_USER, user);
    }

    public void setLikeToStory(Story story) {
        put(KEY_TO_STORY, story);
    }
}
