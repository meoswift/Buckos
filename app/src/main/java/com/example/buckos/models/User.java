package com.example.buckos.models;

import androidx.annotation.Nullable;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Objects;

@ParseClassName("_User")
@Parcel(analyze = {User.class})
public class User extends ParseUser {

    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_INTERESTS = "interests";
    private String followedBy = "";
    private String interestedIn = "";

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

    public void setProfilePic(ParseFile photoFile) {
        put(User.KEY_PROFILE_PIC, photoFile);
    }

    public ParseRelation<Category> getInterests() {
        return getRelation(KEY_INTERESTS);
    }

    public void setSuggestionReason(String reason) {
        followedBy = reason;
    }

    public String getSuggestionReason() {
        return followedBy;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        User user = (User) obj;
        return Objects.equals(getObjectId(), user.getObjectId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectId());
    }
}
