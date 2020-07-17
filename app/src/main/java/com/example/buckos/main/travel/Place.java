package com.example.buckos.main.travel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

// This class represent a Place in the list of point of interests in a city
@Parcel
public class Place {
    private String mName;
    private String mAddressName;
    private Double mRating;
    private int mUserRatingsTotal;

    // no-arg, empty constructor required for Parceler
    public Place() {};

    // initialize a place with needed properties
    // get these properties from json object passed in
    public Place(JSONObject data) throws JSONException {
        mName = data.getString("name");
        mAddressName = data.getString("formatted_address");
        mRating = data.getDouble("rating");
        mUserRatingsTotal = data.getInt("user_ratings_total");
    }

    // create a list of Place objects by parsing the JSONArray of places
    public static List<Place> jsonToList(JSONArray jsonPlaces) throws JSONException {
        List<Place> placeList = new ArrayList<>();

        for (int i = 0; i < jsonPlaces.length(); i++) {
            JSONObject placeObject = jsonPlaces.getJSONObject(i);
            Place place = new Place(placeObject);
            placeList.add(place);
        }

        return placeList;
    }

    public String getName() {
        return mName;
    }

    public String getAddressName() {
        return mAddressName;
    }

    public Float getRating() {
        return mRating.floatValue();
    }

    public String getUserRatingsTotal() {
        return String.format("(%d)", mUserRatingsTotal);
    }
}
