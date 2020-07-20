package com.example.buckos;

import com.parse.ParseClassName;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("_User")
@Parcel(analyze={User.class})
public class User extends ParseUser {

    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";
    public static final String KEY_USERNAME = "username";

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public void setBio(String bio) {
        put(KEY_BIO, bio);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public String getBio() {
        return getString(KEY_BIO);
    }

}
