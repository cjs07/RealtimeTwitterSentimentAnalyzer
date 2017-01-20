package com.deepwelldevelopment.twitteranalysis.core;

import com.deepwelldevelopment.twitteranalysis.TweetCollector;
import com.deepwelldevelopment.twitteranalysis.auth.Authorization;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TweetCollectorMain {

    static Gson gson = new Gson();

    public static void main(String[] args) {
        File authFile = new File("src/main/resources/auth.json");
        Scanner scanner = null;
        try {
            scanner = new Scanner(authFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String json = "";
        if (scanner != null) {
            while (scanner.hasNext()){
                json += scanner.nextLine();
            }
            new TweetCollector(gson.fromJson(json, Authorization.class));
        }
    }
}
