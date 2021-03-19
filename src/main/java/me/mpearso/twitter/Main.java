package me.mpearso.twitter;

import twitter4j.TwitterFactory;

public class Main {

    public static void main(String[] args) {
//        new TwitterRecommendations();
        long now = System.currentTimeMillis();
        new KeywordExtraction()
                .extract("Mic Mic Mic Mic Chig Chig Chig Petr Mic Petr Chig Mic Mic Chig")
                .print();
        System.out.println((System.currentTimeMillis() - now) + "ms");
    }
}
