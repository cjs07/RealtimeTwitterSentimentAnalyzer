package com.deepwelldevelopment.twitteranalysis.json;

import com.deepwelldevelopment.twitteranalysis.core.Tweet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Deserializer {

    private Gson gson;

    public Deserializer() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public Tweet deserialize(String json) {
        return gson.fromJson(json, Tweet.class);
    }
}
