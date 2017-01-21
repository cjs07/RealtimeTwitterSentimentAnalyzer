package com.deepwelldevelopment.twitteranalysis;

import com.deepwelldevelopment.twitteranalysis.auth.Authorization;
import com.deepwelldevelopment.twitteranalysis.core.Tweet;
import com.deepwelldevelopment.twitteranalysis.csv.CSVWriter;
import com.deepwelldevelopment.twitteranalysis.json.Deserializer;
import com.deepwelldevelopment.twitteranalysis.json.Serializer;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TweetCollector implements Runnable {

    //Thread for asynchronous tweet polling
    private Thread thread = new Thread(this);


    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>(100000);
    private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

    //Twitter connection
    private Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
    private StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
    private Client client;

    //json
    private Serializer serializer;
    private Deserializer deserializer;

    //file io
    private File tweetFile;
    private PrintWriter writer;
    private CSVWriter csvWriter;

    private TweetCollector(String consumer, String consumerSecret, String token, String tokenSecret) {
        Authentication auth = new OAuth1(consumer, consumerSecret, token, tokenSecret);
        ClientBuilder builder = new ClientBuilder()
                .name("Tweet Collector-Client-01")
                .hosts(hosts)
                .authentication(auth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(messageQueue))
                .eventMessageQueue(eventQueue);

        client = builder.build();
        // Attempts to establish a connection.
        client.connect();

        serializer = new Serializer();
        deserializer = new Deserializer();

        tweetFile = new File("src/main/resources/tweets.txt");
        try {
            writer = new PrintWriter(tweetFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        csvWriter = new CSVWriter();

        thread.start();
    }

    public TweetCollector(Authorization auth) {
        this(auth.getConsumer(), auth.getConsumerSecret(), auth.getToken(), auth.getTokenSecret());
    }

     //Used for asynchronous collection of tweets
    public void run() {
        //collect a single tweet and print it
        while(!client.isDone()) {
            String message = "";
            try {
                message = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //convert json string from twitter into Tweet object
            Tweet tweet = deserializer.deserialize(message);

            //tweet is in english and actually exists
            if (tweet != null && tweet.getLang() != null && tweet.getLang().equals("en")) {
                System.out.println(tweet);
                writer.append(serializer.serialize(tweet)).append("\n"); //write tweet to file in json format
                writer.flush();
                try {
                    csvWriter.write(tweet); //write tweet to csv file
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.stop(); //disconnects the client
    }
}
