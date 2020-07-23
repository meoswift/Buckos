package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("_User")
@Parcel(analyze = {User.class})
public class User extends ParseUser {

    public static final String KEY_NAME = "name";
    public static final String KEY_BIO = "bio";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_FOLLOWING = "following";


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

    public ParseFile getProfilePic() {
        return getParseFile(KEY_PROFILE_PIC);
    }

    public ParseRelation<User> getFollowingUsers() {
        return getRelation(KEY_FOLLOWING);
    }

}
