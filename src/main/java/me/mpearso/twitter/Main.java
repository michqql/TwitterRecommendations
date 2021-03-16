package me.mpearso.twitter;

import twitter4j.TwitterFactory;

public class Main {

    public static void main(String[] args) {
//        new TwitterRecommendations();
        long now = System.currentTimeMillis();
        new KeywordExtraction()
                .extract("This sentence contains some important words and nuclear key codes that I would hate if someone, such as the Russians got access to.")
                .extract("Important words must be counter as part of this algorithm.")
                .print();
        System.out.println((System.currentTimeMillis() - now) + "ms");
    }
}
