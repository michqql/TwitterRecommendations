package me.mpearso.twitter.interest;

import me.mpearso.twitter.keyword.SimpleKeywordExtractionAlgorithm;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class InterestHandler {

    // The Twitter API instance
    private final Twitter api;

    // The account of the person using the program
    private final User selfUser;

    // The abstract keyword extraction implementation - different algorithms can be used
    // Variable is final so that terms extracted cannot be overwritten during runtime
    // as these terms are kept in the object
    private final SimpleKeywordExtractionAlgorithm impl;
    private final double termThreshold = 0.0D;

    // Class to get Twitter accounts that can be used for recommendations
    private final AccountGenerator accountGenerator;

    private RateLimitStatus recentStatus;
    private List<String> interestsOfSelfUser;
    private final List<User> accountsToProcess = new ArrayList<>();

    public InterestHandler(Twitter api, User selfUser, Consumer<List<Recommendation>> onProcessFinish) {
        this.api = api;
        this.selfUser = selfUser;
        this.impl = new SimpleKeywordExtractionAlgorithm();
        this.accountGenerator = new AccountGenerator(api, selfUser);

        // Get the interests of the user of this program
        this.interestsOfSelfUser = generateInterestsFromUser(selfUser);

        // Start execution in background of retrieving Twitter accounts
        process(onProcessFinish);
    }

    public void process(Consumer<List<Recommendation>> onProcessFinish) {
        accountGenerator.getUsersAsync(
                AccountGenerator.SearchMethod.FOLLOWING_CONNECTIONS,
                20,
                new AccountGenerator.Response() {
                    @Override
                    public void response(List<User> result) {
                        accountsToProcess.addAll(result);
                    }

                    @Override
                    public void lastResponse() {
                        List<Recommendation> recommendations = getRecommendations();
                        onProcessFinish.accept(recommendations);
                    }
                }
        );
    }

    public List<Recommendation> getRecommendations() {
        List<Recommendation> result = new ArrayList<>();

        // Iterate through accounts
        for(User user : accountsToProcess) {

            // Get the interests of this user
            List<String> interests = generateInterestsFromUser(user);

            // The account was not fit to process
            if(interests.isEmpty())
                continue;

            // Compare these interests (only common interests will be kept)
            interests.retainAll(interestsOfSelfUser);

            // These users have no interests in common and so should be skipped
            if(interests.isEmpty())
                continue;

            // The percentage of matching interests
            double weight = (double) interests.size() / interestsOfSelfUser.size();
            result.add(new Recommendation(user, weight, interests));
        }

        accountsToProcess.clear();
        return result;
    }

    public List<String> generateInterestsFromUser(User user) {
        if(user == null || user.isProtected())
            return Collections.emptyList();

        // Check if we have sufficient API calls to complete this
        if(recentStatus != null && recentStatus.getRemaining() <= 0)
            return Collections.emptyList();

        // Ignore any users that do not speak english
        if(selfUser.getLang() != null && !selfUser.getLang().equalsIgnoreCase(user.getLang()))
            return Collections.emptyList();

        try {
            ResponseList<Status> tweetsByUser = api.getUserTimeline(user.getId());
            this.recentStatus = tweetsByUser.getRateLimitStatus();
            for(Status tweet : tweetsByUser) {
                impl.extract(tweet.getText());
            }

            List<String> result = new ArrayList<>();
            impl.getTermFrequencies().forEach((term, freq) -> {
                if(freq >= termThreshold)
                    result.add(term);
            });
            impl.clearTerms();
            return result;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void setInterestsOfSelfUser(List<String> interests) {
        if(interests == null) {
            this.interestsOfSelfUser = generateInterestsFromUser(selfUser);
        } else {
            this.interestsOfSelfUser = interests;
        }
    }
}
