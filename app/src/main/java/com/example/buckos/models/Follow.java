package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

// Follow object represents the Follow relationship between one user to another. When an user
// follow a new user, a new Follow object is created
@ParseClassName("Follow")
@Parcel(analyze = {Follow.class})
public class Follow extends ParseObject {
    public static final String KEY_FROM = "from";
    public static final String KEY_TO = "to";

    public User getFrom() {
        return (User) getParseUser(KEY_FROM);
    }

    public void setFrom(User user) {
        put(KEY_FROM, user);
    }

    public User getTo() {
        return (User) getParseUser(KEY_TO);
    }

    public void setTo(User user) {
        put(KEY_TO, user);
    }
}

