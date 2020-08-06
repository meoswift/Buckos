package com.example.buckos.models;

import com.example.buckos.R;

import java.util.ArrayList;
import java.util.List;

public class Suggestion {

    private String mTitle;
    private String mDescription;
    private String mImageUrl;

    public Suggestion() { }

    public Suggestion(String title, String imageUrl, String description) {
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = description;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    // Returns a list of contacts
    public static List<Suggestion> getTravelSuggestions() {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Get stunned by the Great Pyramid of Giza", "https://tourscanner.com/blog/wp-content/uploads/2018/04/pyramids-of-giza-egypt-1.jpg", "The Great Pyramid of Giza defines bucket list goals. Built over 5000 years ago, it’s the oldest and the biggest of the pyramids, the last structure of the Seven Ancient Wonders of the World that survived."));
        suggestions.add(new Suggestion("Walk along the Great Wall of China", "https://tourscanner.com/blog/wp-content/uploads/2018/04/great-wall-of-china.jpg", "Identified as one of the most important symbols of China, it consists of numerous walls and fortification, many running parallels to each other for a measure of 8,850 km. The Great Wall can be visited from Beijing."));
        suggestions.add(new Suggestion("Explore Petra", "https://tourscanner.com/blog/wp-content/uploads/2018/04/petra.jpg?x65729", "Jordan’s rose-colored sandstone city is an extraordinary experience. On the border of the Arabian desert, hidden in the mountains of the Dead Sea, Petra is one of the most notorious archeological sites in the world."));
        suggestions.add(new Suggestion("Visit the Colosseum", "https://tourscanner.com/blog/wp-content/uploads/2018/04/colosseum.jpg", "Recognized as one of the Seven Wonders of the World, Rome’s great gladiatorial arena is the most exciting of the city’s ancient sights. "));
        suggestions.add(new Suggestion("Walk to Chichen Itza", "https://tourscanner.com/blog/wp-content/uploads/2018/04/chichen-itza.jpg?x65729", "Located in the Yutacan state of Mexico, Chichen Itza is the best place to visit in the world if you are interested in an ancient Mayan city featuring ruins and architectural wonders that captures the culture and history of the people from the past."));
        return suggestions;
    }


}
