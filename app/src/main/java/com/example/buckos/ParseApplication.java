package com.example.buckos;

import android.app.Application;

import com.example.buckos.models.BucketList;
import com.example.buckos.models.Category;
import com.example.buckos.models.Comment;
import com.example.buckos.models.Follow;
import com.example.buckos.models.Item;
import com.example.buckos.models.Like;
import com.example.buckos.models.Photo;
import com.example.buckos.models.Search;
import com.example.buckos.models.User;
import com.example.buckos.models.Story;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

// This class initializes connection to Parse database
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Register parse models
        ParseObject.registerSubclass(BucketList.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Item.class);
        ParseObject.registerSubclass(Photo.class);
        ParseObject.registerSubclass(Story.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Follow.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Search.class);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id)) // should correspond to APP_ID env variable
                .clientKey(getString(R.string.back4app_client_key))
                .server("https://parseapi.back4app.com/").build());
    }

}
