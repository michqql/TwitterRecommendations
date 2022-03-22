package me.mpearso.twitter.interest;

import twitter4j.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class AccountGenerator {

    public enum SearchMethod {
        FOLLOWING_CONNECTIONS, TRENDING_HASHTAG
    }

    public interface Response {
        void response(List<User> result);
        void lastResponse(List<User> finalState);
    }

    private final static long FIFTEEN_MINUTES_IN_MILLIS = TimeUnit.MINUTES.toMillis(15);

    private final Twitter api;
    private final User user;

    public AccountGenerator(Twitter api, User user) {
        this.api = api;
        this.user = user;
    }

    public void getUsersAsync(SearchMethod searchMethod, Response response) {
        // New thread: code runs on a different thread to the main and therefore is asynchronous
        Thread thread = new Thread(() -> {
            List<User> result = null;
            // Switches the method used in account retrieval based on the supplied searchMethod
            try {
                switch (searchMethod) {
                    case FOLLOWING_CONNECTIONS:
                        result = getUsersByFollowing(response, 100);
                        break;

                    case TRENDING_HASHTAG:
                        result = getUsersByTrendingHashtag();
                        break;

                    default:
                        result = Collections.emptyList();
                }
            } catch (TwitterException e) {
                System.out.println("Rate limited!");
                RateLimitStatus status = e.getRateLimitStatus();
                System.out.println("Remaining: " + status.getRemaining());
                System.out.println("Till reset: " + status.getSecondsUntilReset());
            } catch (InterruptedException ignore) {}

            // Returns the result through a consumer, so the code will still be run asynchronously
            response.lastResponse(result);
        });

        thread.start();
    }

    private List<User> getUsersByFollowing(Response response, final int amount) throws InterruptedException, TwitterException {
        System.out.println("Method");
        //int followingNumber = user.getFriendsCount(); // The number of accounts followed by this user

        // A list of accounts that the user follows (grouped by 20)
        // The user already follows these accounts, so we don't want
        // to add them to the result, as these would not be good recommendations
        List<User> following = getFollowing(user, 1); // One API call used
        System.out.println("Following initial size: " + following.size());
        List<User> result = new ArrayList<>();

        int apiCalls = 14;

        // Queue data structure to implement DFS without recursion
        LinkedList<User> toVisit = new LinkedList<>(following);
        while(result.size() < amount && !toVisit.isEmpty()) {
            System.out.println("Loop");
            // The current node at the head of the queue
            User currentNode = toVisit.poll();
            if(currentNode == null)
                break;

            // Get the children of the current node
            int followingNumber = currentNode.getFriendsCount();
            int groups = (int) Math.min(apiCalls, Math.ceil(followingNumber / 20D));
            List<User> currentFollowing = getFollowing(currentNode, groups);

            // Deduct from the API calls, as we will have used these to retrieve X groups
            apiCalls -= groups;
            for(User nextNode : currentFollowing) {
                toVisit.addFirst(nextNode);
                result.add(nextNode);
            }

            // If we have run out of API calls, return the accounts we currently have
            // and wait for 15 minutes
            if(apiCalls <= 0) {
                response.response(result);
                wait(FIFTEEN_MINUTES_IN_MILLIS);
            }
        }

        return result;
    }

    private List<User> getUsersByTrendingHashtag() throws TwitterException {
        return new ArrayList<>();
    }

    private List<User> getFollowing(User user, int groups) throws TwitterException {
        // API call retrieves friends in groups of 20.
        // API method is rate limited and allows for 15 calls every 15 minutes.
        // Therefore, we can only retrieve maximum of 300 (20*15) users every 15 minutes.

        // The amount of api calls we can expend on this operation
        int apiCalls = groups;

        List<User> result = new ArrayList<>();

        PagableResponseList<User> response;
        long cursor = -1;
        while(cursor != 0) {
            // Get group of 20 and update cursor to next group
            response = api.getFriendsList(user.getId(), cursor);
            cursor = response.getNextCursor(); // Get next cursor will return 0 when there are no more groups
            // and will break out of the while loop.

            // Add all users from current group response to the result
            result.addAll(response);

            // Checks to see how many api calls have been used so far
            // If we have used up the desired amount, return what results we have so far
            apiCalls--;
            if(apiCalls <= 0)
                return result;
        }

        return result;
    }
}
