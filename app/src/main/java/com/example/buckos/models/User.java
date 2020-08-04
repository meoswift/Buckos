package com.example.buckos.models;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.List;
import java.util.Objects;

// This class represents an User - with information like name, username, bio, following suggestions
// and interests.
@ParseClassName("_User")
@Parcel(analyze = {User.class})
public class User extends ParseUser {

    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_INTERESTS = "interests";
    public static final String KEY_FRIENDS = "friends";
    private String followedBy = "";
    public String interestedIn = "";

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

    public ParseRelation<User> getFriends() {
        return getRelation(KEY_FRIENDS);
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
