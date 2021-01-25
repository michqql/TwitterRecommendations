package me.mpearso.twitter.component;

import twitter4j.Status;
import twitter4j.User;

import java.io.*;

public class Tweet {

    private final User tweeter;
    private final Type type;
    private final String text;
    private final int likes, retweets;
    private final String url;

    public Tweet(Status status) {
        this.tweeter = status.getUser();
        this.type = (status.isRetweet() ? Type.RT : Type.TWEET);
        this.text = (type == Type.RT) ? status.getRetweetedStatus().getText() : status.getText();
        this.likes = status.getFavoriteCount();
        this.retweets = status.getRetweetCount();
        this.url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
    }

    public void print() {
        System.out.println("Tweeter: " + tweeter.getName() + ", " + tweeter.getId());
        System.out.println("Text: " + text);
        System.out.println("Link: " + url);
        System.out.println("Type: " + type.toString());
        System.out.println("Likes: " + likes + ", retweets: " + retweets);
    }

    public void toFile() {
        File file = new File(tweeter.getName() + ".txt");
        try {
            file.createNewFile();
            System.out.println("Created file at " + file.getAbsolutePath());

            FileWriter myWriter = new FileWriter(file);
            myWriter.write(this.text);
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum Type {
        TWEET, RT
    }
}
