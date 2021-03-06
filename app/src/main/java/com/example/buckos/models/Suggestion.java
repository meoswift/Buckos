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

    // Returns a list of travel suggestions
    public static List<Suggestion> getTravelSuggestions() {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Get stunned by the Great Pyramid of Giza", "https://tourscanner.com/blog/wp-content/uploads/2018/04/pyramids-of-giza-egypt-1.jpg", "The Great Pyramid of Giza defines bucket list goals. Built over 5000 years ago, it’s the last structure of the Seven Ancient Wonders of the World that survived."));
        suggestions.add(new Suggestion("Walk along the Great Wall of China", "https://tourscanner.com/blog/wp-content/uploads/2018/04/great-wall-of-china.jpg", "Identified as one of the most important symbols of China, it consists of numerous walls and fortification, many running parallels to each other for a measure of 8,850 km."));
        suggestions.add(new Suggestion("Explore Petra", "https://tourscanner.com/blog/wp-content/uploads/2018/04/petra.jpg?x65729", "Jordan’s rose-colored sandstone city is an extraordinary experience. On the border of the Arabian desert, hidden in the mountains of the Dead Sea, Petra is one of the most notorious archeological sites in the world."));
        suggestions.add(new Suggestion("Visit the Colosseum", "https://tourscanner.com/blog/wp-content/uploads/2018/04/colosseum.jpg", "Recognized as one of the Seven Wonders of the World, Rome’s great gladiatorial arena is the most exciting of the city’s ancient sights. "));
        suggestions.add(new Suggestion("Walk to Chichen Itza", "https://tourscanner.com/blog/wp-content/uploads/2018/04/chichen-itza.jpg?x65729", "Located in the Yutacan state of Mexico, Chichen Itza is the best place to visit in the world if you are interested in an ancient Mayan city featuring ruins and architectural wonders that captures the culture and history of the people from the past."));
        suggestions.add(new Suggestion("Sail on Ha Long Bay", "https://tourscanner.com/blog/wp-content/uploads/2019/09/12-Halong-Bay-Day-Trip.jpg", "The ethereal beauty, Ha Long Bay is located in the area of the Gulf of Tangkia, in the north of Vietnam."));
        suggestions.add(new Suggestion("Visit the Acropolis of Athens", "https://tourscanner.com/blog/wp-content/uploads/2018/04/acropolis-of-athens.jpg", "The Acropolis of Athens and its monuments are the universal symbol of the classical spirit and civilization and form the most significant architectural and artistic complex of the Greek antiquity."));
        suggestions.add(new Suggestion("Visit Easter Island", "https://tourscanner.com/blog/wp-content/uploads/2018/04/easter-islands.jpg", "Who wouldn’t want the opportunity to visit one of the most isolated and mysterious islands on Earth?"));
        return suggestions;
    }

    // Returns a list of contacts
    public static List<Suggestion> getFoodSuggestions() {
        List<Suggestion> suggestions = new ArrayList<>();
        suggestions.add(new Suggestion("Fresh coconuts, St Lucia", "https://www.telegraph.co.uk/content/dam/Travel/2017/January/food%201.jpg?imwidth=1240", "Coconuts’ nutrient-rich water is best slurped straight from the fruit itself. On a St Lucia beach (any will do), a few coins will buy you a green-golden orb, freshly cut from a leggy palm."));
        suggestions.add(new Suggestion("Hunt for Truffles", "https://i0.wp.com/bucketlistjourney.net/wp-content/uploads/2016/10/IMG_4185-copy.jpg", "A truffle is an underground fungus that expert chefs consider a culinary delicacy due to their rarity and flavor. Because of this, hunting for them yourself may be a better option for the pocket book, and a much more fun experience."));
        suggestions.add(new Suggestion("Create Your Own Cocktail", "https://i0.wp.com/bucketlistjourney.net/wp-content/uploads/2016/10/IMG_3770-copy.jpg", "Many foodies are also experimentalists. The best part about this bucket list goal is that you will have to do a lot of sampling before coming up with your own perfect harmony of flavors."));
        suggestions.add(new Suggestion("Make Fresh Pasta", "https://i0.wp.com/bucketlistjourney.net/wp-content/uploads/2014/09/IMG_8883-800x533.jpg", "There is nothing like the taste of fresh pasta and it is fairly easy to make, using only 3 ingredients you can make fettuccini, pappardelle or tagliatelle."));
        suggestions.add(new Suggestion("Tour a Vineyard", "https://i0.wp.com/bucketlistjourney.net/wp-content/uploads/2013/06/IMG_8319-800x533.jpg", "Vineyards all over the world offer tours that will take you through the vineyards, to the barrel room and finish with tasting."));
        suggestions.add(new Suggestion("Pick food from a tree and make a pie", "https://i0.wp.com/bucketlistjourney.net/wp-content/uploads/2016/10/14292250_10154307492091858_5663692881972049872_n.jpg", "There’s an art to making the perfect homemade pie. It’s all about the freshness of the fruit, creating the perfect pie crust and making it with a touch of love."));
        suggestions.add(new Suggestion("Make and Bottle Your Own Wine", "https://i0.wp.com/bucketlistjourney.net/wp-content/uploads/2012/09/IMG_4132-800x533.jpg", "You don’t need to own a vineyard to make and bottle your own wine. You can either create your own at home or work with a vineyard, many of which will help you to create the perfect blend for a small batch of vino."));
        suggestions.add(new Suggestion("Eat Only Ice Cream For an Entire Day", "https://s3.amazonaws.com/secretsaucefiles/photos/images/000/158/239/large/2017-04-14_07.41.04_1.jpg?1492282056", "What better way to show your love for ice cream, probably the best dessert ever, than eating it for an entire day."));

        return suggestions;
    }

    public static List<Suggestion> getMovieSuggestions() {
        List<Suggestion> suggestions = new ArrayList<>();

        suggestions.add(new Suggestion("Bohemian Rhapsody (2018)", "https://rooftopfilmclub.com/wp-content/uploads/2018/01/bohemian-rhapsody-rami-malek.gif", "This won more than best motion picture at the Golden Globes, it won an entire generation. If you weren’t a fan of Queen before, then you are now." ));
        suggestions.add(new Suggestion("One Flew Over the Cuckoo’s Nest (1975)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/giphy-3-1.gif", "It’s the story of Randle McMurphy, who, despite not being mentally ill, is moved from prison to a mental institution. McMurphy ends up leading a revolution and starts to plan an escape. It does not go well."));
        suggestions.add(new Suggestion("Shawshank Redemption (1994)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/Shawshank-Redemption-1.gif", "Adapted from Stephen King’s evidently limitless mind, the American drama follows Andy Dufresne, a banker who is sentenced to life in Shawshank State Penitentiary for the murder of his wife and her lover, despite his innocent pleas."));
        suggestions.add(new Suggestion("Lady Bird (2017)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/lady-birdd.gif", "Calling herself “Lady Bird” is just one way she’s taking teenage rebellion to a whole new level. All she wants to do is escape to somewhere where she can lap up culture and be around people who aren’t ridiculously boring."));
        suggestions.add(new Suggestion("Inside Out (2015)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/inside-out-1.gif", "Inside Out is now being used all over to help teach kids about emotions.  And we miss our emotional stability. It’s powerful, moving, and everyone should watch it."));
        suggestions.add(new Suggestion("La La Land (2016)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/la-la-land-1-1.gif", " Lose yourself in this glorious musical where two struggling stars devote every second to making it big. You’ll be dancing in your seat, singing along, and highly emotional in no time. "));
        suggestions.add(new Suggestion("The Breakfast Club (1985)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/The-Breakfast-club-1.gif", "This coming-of-age comedy is p. much one of the greatest high school films ever made. Ever. The story sees five teenagers all brought together in a Saturday detention as they realise that there is more to them than their cliques."));
        suggestions.add(new Suggestion("Back to the Future (1985)", "https://rooftopfilmclub.com/wp-content/uploads/2018/03/giphy-1-1.gif", "The sci-fi adventure comedy is the first film of, basically, one of the best trilogies in the universe. Michael J. Fox is the mega dreamy Marty McFly who goes back in time to 1955"));

        return suggestions;
    }


}
