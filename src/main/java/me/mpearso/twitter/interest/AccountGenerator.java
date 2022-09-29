package me.mpearso.twitter.interest;

import twitter4j.*;
import java.util.*;

@SuppressWarnings("ALL")
public class AccountGenerator {

    public enum SearchMethod {
        FOLLOWING_CONNECTIONS, TRENDING_HASHTAG
    }

    public interface Response {
        void response(List<User> result);
        void lastResponse();
    }

    private final Twitter api;
    private final User user;

    public AccountGenerator(Twitter api, User user) {
        this.api = api;
        this.user = user;
    }

    public void getUsersAsync(SearchMethod searchMethod, int amount, Response response) {
        // New thread: code runs on a different thread to the main and therefore is asynchronous
        Thread thread = new Thread(() -> {
            // Switches the method used in account retrieval based on the supplied searchMethod
            try {
                switch (searchMethod) {
                    case FOLLOWING_CONNECTIONS:
                        getUsersByFollowing(response, amount);
                        break;

                    case TRENDING_HASHTAG:
                        // TODO: Implementation
                        break;
                }
            } catch (TwitterException | InterruptedException ignore) {}

            // Returns the result through a consumer, so the code will still be run asynchronously
            response.lastResponse();
        });

        thread.start();
    }

    private void getUsersByFollowing(final Response response, final int amountOfAccounts)
            throws InterruptedException, TwitterException {

        // Check rate limit status of friend id calls
        RateLimitStatus rateLimitStatus = getRateLimitStatus("/friends/ids");
        if(rateLimitStatus.getRemaining() <= 0) {
            Thread.sleep((rateLimitStatus.getSecondsUntilReset() + 2) * 1000L);
        }

        // A list of accounts that the user follows (grouped by 3500)
        // The user already follows these accounts, so we don't want
        // to add them to the result, as these would not be good recommendations
        List<Long> following = getFollowing(user.getId(), 1); // One API call used
        List<User> result = new ArrayList<>();

        // Start a timer that will respond to the main thread with the updates at a regular interval
        // while new accounts are being processed.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(result.size() > 0) {
                    response.response(result);
                    result.clear();
                }
            }
        }, 10000L, 10000L);

        int traversed = 0; // The number of accounts traversed

        // Queue data structure to implement DFS without recursion
        LinkedList<Long> toVisit = new LinkedList<>(following);
        while(traversed < amountOfAccounts && !toVisit.isEmpty()) {
            // The current node at the head of the queue
            Long currentNode = toVisit.poll();
            if(currentNode == null)
                break;

            // Get the children of the current node
            List<Long> currentFollowing = getFollowing(currentNode, 1);

            for(Long nextNode : currentFollowing) {
                toVisit.addFirst(nextNode);

                // Add the account to the result
                User user = api.showUser(nextNode);
                if(user == null)
                    continue;

                result.add(user);

                // If we have exceeded call limit, timeout until it has refreshed
                if(user.getRateLimitStatus().getRemaining() <= 1) {
                    Thread.sleep((rateLimitStatus.getSecondsUntilReset() + 2) * 1000L);
                }

                // Add to the number of accounts retrieved
                // as we only want to retrieve up to amountOfAccounts
                traversed++;
                if(traversed >= amountOfAccounts)
                    break;
            }

            // Check rate limit status of friend id calls
            rateLimitStatus = getRateLimitStatus("/friends/ids");
            if(rateLimitStatus.getRemaining() <= 0) {
                Thread.sleep((rateLimitStatus.getSecondsUntilReset() + 2) * 1000L);
            }
        }

        response.response(result);
        timer.cancel(); // Stop the timer once the task has finished
    }

    private List<Long> getFollowing(long userId, int groups) throws TwitterException, InterruptedException {
        // getFriendsIDs(userId, cursor) API call retrieves friends in groups of 3500.
        // API method is rate limited and allows for 15 calls every 15 minutes.
        // Therefore, we can only retrieve maximum of 52,500 (3500*15) users every 15 minutes.

        // The amount of api calls we can expend on this operation
        int apiCalls = groups;

        List<Long> result = new ArrayList<>();

        IDs response;
        long cursor = -1;
        while(cursor != 0) {
            // Get group of 3500 and update cursor to next group
            response = api.getFriendsIDs(userId, cursor);
            cursor = response.getNextCursor(); // Get next cursor will return 0 when there are no more groups
            // and will break out of the while loop.

            // Add all users from current group response to the result
            for(long uuid : response.getIDs())
                result.add(uuid);

            // Check the rate limitation status, if we have 0 or fewer calls left, break loop and return
            RateLimitStatus rateLimitStatus = response.getRateLimitStatus();
            if(rateLimitStatus.getRemaining() <= 0)
                break;

            // Checks to see how many api calls have been used so far
            // If we have used up the desired amount, break out of the loop and return
            apiCalls--;
            if(apiCalls <= 0)
                break;
        }
        return result;
    }

    private RateLimitStatus getRateLimitStatus(String endpoint) throws TwitterException {
        return api.getRateLimitStatus().get(endpoint);
    }
}
