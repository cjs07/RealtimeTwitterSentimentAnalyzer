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
import java.util.ArrayList;
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
        ArrayList<String> messages = new ArrayList<String>();
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();
        //collect tweets as long as the client is still running
        while(!client.isDone()) {
            if (messageQueue.size() > 0) {
                for (String s : messageQueue) {
                    messages.add(s);
                    //convert json string from twitter into Tweet object
                    tweets.add(deserializer.deserialize(s));
                }
                messageQueue.clear();
            } else { //if there are no tweets, sleep the thread and try again from beginning of loop
//                try {
//                    Thread.sleep(10);
                    continue;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            //check each tweet to make sure it exists and is in english
            tweets.removeIf(e -> e == null || e.getLang() == null || !e.getLang().equals("en"));
            tweets.forEach(e -> writer.append(serializer.serialize(e)));
            writer.flush();
            try {
                csvWriter.write(tweets.toArray()); //write tweets to csv file
                messages.clear();
                tweets.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.stop(); //disconnects the client
    }
}
