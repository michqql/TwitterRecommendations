package me.mpearso.twitter.interest;

import twitter4j.User;

import java.util.Collections;
import java.util.List;

public class Recommendation {

    private final User twitterUser;
    private final List<String> sharedInterests;
    private final List<User> mutualConnections;

    public Recommendation(User twitterUser) {
        this.twitterUser = twitterUser;
        this.sharedInterests = Collections.emptyList();
        this.mutualConnections = Collections.emptyList();
    }

    public Recommendation(User twitterUser, List<String> sharedInterests, List<User> mutualConnections) {
        this.twitterUser = twitterUser;
        this.sharedInterests = sharedInterests;
        this.mutualConnections = mutualConnections;
    }

    public User getTwitterUser() {
        return twitterUser;
    }

    public List<String> getSharedInterests() {
        return sharedInterests;
    }

    public List<User> getMutualConnections() {
        return mutualConnections;
    }
}
