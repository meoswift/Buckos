package com.example.buckos.main_screen.travel_explore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

// This class represent a Place in the list of point of interests in a city
@Parcel
public class Place {
    String name;
    String addressName;

    // no-arg, empty constructor required for Parceler
    public Place() {};

    // initialize a place with needed properties
    // get these properties from json object passed in
    public Place(JSONObject data) throws JSONException {
        this.name = data.getString("name");
        this.addressName = data.getString("formatted_address");
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
        return name;
    }

    public String getAddressName() {
        return addressName;
    }
}
