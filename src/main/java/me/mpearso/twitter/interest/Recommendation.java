package me.mpearso.twitter.interest;

import twitter4j.User;

import java.util.List;

public class Recommendation {

    private final User twitterUser;
    private final double weight;

    private final List<String> interests; // Shared interests

    public Recommendation(User twitterUser, double weight, List<String> interests) {
        this.twitterUser = twitterUser;
        this.weight = weight;
        this.interests = interests;
    }

    public User getTwitterUser() {
        return twitterUser;
    }

    public double getWeight() {
        return weight;
    }

    public List<String> getInterests() {
        return interests;
    }
}
