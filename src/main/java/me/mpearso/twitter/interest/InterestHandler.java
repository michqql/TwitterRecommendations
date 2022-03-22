package me.mpearso.twitter.interest;

import me.mpearso.twitter.keyword.AbstractKeywordExtractionAlgorithm;
import me.mpearso.twitter.keyword.SimpleKeywordExtractionAlgorithm;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

import java.util.ArrayList;
import java.util.List;

public class InterestHandler {

    // The Twitter API instance
    private final Twitter api;

    // The account of the person using the program
    private final User selfUser;

    // The abstract keyword extraction implementation - different algorithms can be used
    // Variable is final so that terms extracted cannot be overwritten during runtime
    // as these terms are kept in the object
    private final AbstractKeywordExtractionAlgorithm implementation;

    public InterestHandler(Twitter api, User selfUser) {
        this.api = api;
        this.selfUser = selfUser;
        this.implementation = new SimpleKeywordExtractionAlgorithm();
    }

    public InterestHandler(Twitter api, User selfUser, AbstractKeywordExtractionAlgorithm implementation) {
        this.api = api;
        this.selfUser = selfUser;
        this.implementation = implementation;
    }

//    public List<Recommendation> getRecommendations() {
//        // TODO: Get
//    }

    public List<String> generateInterestsFromUser(User user) {
        // TODO: Ignore non-english speakers

        try {
            // Status wraps a Twitter tweet
            List<Status> tweetsByUser = api.getUserTimeline(user.getId());
            System.out.println("Tweets:");
            tweetsByUser.forEach(status -> System.out.println(status.getText()));
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
