package com.deepwelldevelopment.twitteranalysis.json;

import com.deepwelldevelopment.twitteranalysis.core.Tweet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Serializer {

    private Gson gson;

    public Serializer() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public String serialize(Tweet tweet) {
        return gson.toJson(tweet);
    }
}
