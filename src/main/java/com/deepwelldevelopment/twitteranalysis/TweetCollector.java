package com.deepwelldevelopment.twitteranalysis;

import com.deepwelldevelopment.twitteranalysis.auth.Authorization;
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TweetCollector implements Runnable {

    Thread thread = new Thread(this);

    BlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>(100000);
    BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);

    Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
    StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
    Client client;

    public TweetCollector(String consumer, String consumerSecret, String token, String tokenSecret) {
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

        thread.start();
    }

    public TweetCollector(Authorization auth) {
        this(auth.getConsumer(), auth.getConsumerSecret(), auth.getToken(), auth.getTokenSecret());
        System.out.println(auth);
    }

     //Used for asynchronous collection of tweets
    public void run() {
        //collect a single tweet and print it
        System.out.println("Waiting for a tweet...");
        try {
            System.out.println(messageQueue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        client.stop();
    }
}
