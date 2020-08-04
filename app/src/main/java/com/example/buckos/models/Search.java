package com.example.buckos.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("Search")
@Parcel(analyze = {Search.class})
public class Search extends ParseObject {
    public static final String KEY_FROM_USER = "fromUser";
    public static final String KEY_TO_USER = "toUser";
    public static final String KEY_TIME_SEARCHED = "timeSearched";

    public Search() {}

    public Search(User fromUser, User toUser, Date timeSearched) {
        put(KEY_FROM_USER, fromUser);
        put(KEY_TO_USER, toUser);
        put(KEY_TIME_SEARCHED, timeSearched);
    }

    public void modifySearchTime(Date searched) {
        put(KEY_TIME_SEARCHED, searched);
    }

    public User getSearchedUser() {
        return (User) getParseUser(KEY_TO_USER);
    }
}
