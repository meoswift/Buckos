package com.example.buckos.models;

import android.graphics.Color;

import com.example.buckos.R;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class City {
    private String mCityName;
    private String mCountry;

    public City () {}

    // Initialize category for Explore tab
    public City(String name, String country) {
        mCityName = name;
        mCountry = country;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getCountry() {
        return mCountry;
    }

    // Returns a list of contacts
    public static List<City> getCities() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("Salzburg", "Austria"));
        cities.add(new City("Oaxaca", "Mexico"));
        cities.add(new City("Hoi An", "Vietnam"));
        cities.add(new City("Cairo", "Egypt"));
        cities.add(new City("Galway", "Ireland"));
        cities.add(new City("New York City", "United States"));
        cities.add(new City("London", "England"));
        cities.add(new City("Tokyo", "Japan"));
        cities.add(new City("Rome", "Italy"));
        cities.add(new City("Sydney", "Australia"));

        return cities;
    }
}
