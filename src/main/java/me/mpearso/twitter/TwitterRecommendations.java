package me.mpearso.twitter;

import me.mpearso.twitter.account.LoginHandler;
import me.mpearso.twitter.component.Tweet;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class TwitterRecommendations {

    private final String
            API_KEY = "87P5aD2zEvKlmGFPCVqxrYLcO",
            API_SECRET = "mRMGydcPAoKwzcQ5E3hMA78vQ6dzCXeFYFTrkviol0z1g0xTnP",
            ACCESS_TOKEN = "1058101469684076544-jglOOVaOhVkq1dSsPEgannfom5Seyu",
            ACCESS_SECRET = "Df072m6zOCPWjJrdbQvCCkdYx9NvDmWo4HDYfpGAXKNl4";





















    private Twitter twitter;
    private String twitterHandle;
    private User selfUser;

    public TwitterRecommendations() {
        loginModularised();
    }

    private void loginModularised() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(API_KEY)
                .setOAuthConsumerSecret(API_SECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        final LoginHandler loginHandler = new LoginHandler(twitter);

        try {
            for (Status status : twitter.getHomeTimeline()) {
                System.out.println("---------------------------------------------------------");
                new Tweet(status).print();
            }
        } catch(TwitterException ignore) {}
    }

    private void loginAdvanced() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(API_KEY)
                .setOAuthConsumerSecret(API_SECRET);

        try {
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            try {
                System.out.println("-----------");
                RequestToken requestToken = twitter.getOAuthRequestToken();


                System.out.println("Request Token: " + requestToken.getToken());
                System.out.println("Request Token Secret: " + requestToken.getTokenSecret());
                System.out.println("-----------");

                AccessToken accessToken = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                while(accessToken == null) {
                    System.out.println(requestToken.getAuthenticationURL());
                    System.out.println("Enter the pin: ");

                    String pin = br.readLine();
                    if(!pin.isEmpty()) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken(requestToken);
                    }
                    twitter.setOAuthAccessToken(accessToken);
                }

                System.out.println("-----------");
                System.out.println("Access Token: " + accessToken.getToken());
                System.out.println("Access Token Secret: " + accessToken.getTokenSecret());


            } catch (TwitterException | IOException e) {
                e.printStackTrace();
                if(!twitter.getAuthorization().isEnabled()) {
                    System.out.println("OAuth not set");
                    System.exit(1);
                }
            }

            List<Status> statuses = twitter.getHomeTimeline();
            for (Status status : statuses) {
                System.out.println("---------------------------------------------------------");
                new Tweet(status).print();
            }

            new Tweet(statuses.get(0)).toFile();
        } catch (TwitterException e) {
            e.printStackTrace();
            System.out.println("Failed to get timeline: " + e.getMessage());
        }
    }

    private void loginDefault() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(API_KEY)
                .setOAuthConsumerSecret(API_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_SECRET).setTweetModeExtended(true);

        TwitterFactory tf = new TwitterFactory(cb.build());
        this.twitter = tf.getInstance();

        Scanner in = new Scanner(System.in);
        this.twitterHandle = in.nextLine();
        System.out.println("You entered: " + twitterHandle);

        try {
            this.selfUser = this.twitter.showUser(this.twitterHandle);
            System.out.println("Self User: " + selfUser.getName() + " " + selfUser.getId());

            List<Status> statuses = this.twitter.getUserTimeline(selfUser.getId());
            System.out.println("Showing feed...");

            for(Status status : statuses) {
                System.out.println("---------------------------------------------------------");
                new Tweet(status).print();

            }

            new Tweet(statuses.get(0)).toFile();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
