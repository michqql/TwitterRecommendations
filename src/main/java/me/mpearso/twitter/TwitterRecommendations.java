package me.mpearso.twitter;

import me.mpearso.twitter.gui.TRMainWindow;
import me.mpearso.twitter.login.LoginHandler;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterRecommendations {

    private final static String
            API_KEY = "87P5aD2zEvKlmGFPCVqxrYLcO",
            API_SECRET = "mRMGydcPAoKwzcQ5E3hMA78vQ6dzCXeFYFTrkviol0z1g0xTnP";

    public static void main(String[] args) {
        new TwitterRecommendations();
    }

    public TwitterRecommendations() {
        init();
    }

    private void init() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(API_KEY)
                .setOAuthConsumerSecret(API_SECRET)
                .setTweetModeExtended(true);

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        final LoginHandler loginHandler = new LoginHandler(twitter);
        new TRMainWindow(twitter, loginHandler);
    }
}
