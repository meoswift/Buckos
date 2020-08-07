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
    private String mCityImage;

    public City () {}
    // Initialize category for Explore tab
    public City(String name, String country, String imageUrl) {
        mCityName = name;
        mCountry = country;
        mCityImage = imageUrl;
    }

    public String getCityName() {
        return mCityName;
    }

    public String getCountry() {
        return mCountry;
    }


    public String getCityImage() {
        return mCityImage;
    }

    // Returns a list of contacts
    public static List<City> getCities() {
        List<City> cities = new ArrayList<>();
        cities.add(new City("Salzburg", "Austria", "https://tr-images.condecdn.net/image/NvX4vowVkvb/crop/1620/f/salzburg-gettyimages-562610487.jpg"));
        cities.add(new City("Quebec City", "Canada", "https://tr-images.condecdn.net/image/nVdNZLPR5AA/crop/1620/f/quebec-gettyimages-155443295.jpg"));
        cities.add(new City("Hoi An", "Vietnam", "https://i2.wp.com/storage.googleapis.com/inspitrip-blog/global/2018/05/hoi-an-at-night.jpg?fit=750%2C501&ssl=1"));
        cities.add(new City("Paris", "France", "https://tr-images.condecdn.net/image/jJDz5Zq6Jev/crop/1020/f/22_most-instagrammable-places-in-paris-conde-nast-traveller-6nov17-mary-quincy.jpg"));
        cities.add(new City("Amsterdam", "Netherlands", "https://tr-images.condecdn.net/image/W54E5Gd0y4e/crop/1620/f/amsterdam-gettyimages-480360610.jpg"));
        cities.add(new City("New York City", "United States", "https://blog-www.pods.com/wp-content/uploads/2019/04/MG_1_1_New_York_City-1.jpg"));
        cities.add(new City("London", "England", "https://lp-cms-production.imgix.net/2019-06/55425108.jpg?fit=crop&q=40&sharp=10&vib=20&auto=format&ixlib=react-8.6.4"));
        cities.add(new City("Tokyo", "Japan", "https://mymodernmet.com/wp/wp-content/uploads/2020/02/ludwig-favre-tokyo-1.jpg"));
        cities.add(new City("Rome", "Italy", "https://tr-images.condecdn.net/image/zklqQBdOn8L/crop/1620/f/gettyimages-901109164.jpg"));
        cities.add(new City("Sydney", "Australia", "https://lp-cms-production.imgix.net/2019-06/65830387.jpg?sharp=10&vib=20&w=1200"));

        return cities;
    }
}
