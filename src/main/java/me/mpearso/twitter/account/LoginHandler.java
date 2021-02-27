package me.mpearso.twitter.account;

import me.mpearso.twitter.data.text.KeyValueTextFile;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginHandler {

    private final Twitter twitter;
    private final KeyValueTextFile accessTokenFile = new KeyValueTextFile("data", "tokens", ":");
    private Runnable runnable;

    public LoginHandler(final Twitter twitter) {
        this.twitter = twitter;

        if(accessTokenFile.isEmpty()) {
            promptAuth();
        }
    }

    private void promptAuth() {
        try {
            RequestToken requestToken = twitter.getOAuthRequestToken();
            AccessToken accessToken = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while(accessToken == null) {
                System.out.println(requestToken.getAuthenticationURL());
                System.out.println("Enter the pin: ");

                String pin = br.readLine();
                if(!pin.isEmpty()) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken(requestToken);
                }
            }

            accessTokenFile.put("access_token", accessToken.getToken());
            accessTokenFile.put("access_secret", accessToken.getTokenSecret());
            accessTokenFile.save();

            if(runnable != null)
                runnable.run();

        } catch (TwitterException | IOException e) {
            e.printStackTrace();
            if(!twitter.getAuthorization().isEnabled()) {
                System.out.println("OAuth not set");
                System.exit(1);
            }
        }
    }

    public void onAuthentication(Runnable run) {
        this.runnable = run;
        if(!accessTokenFile.isEmpty())
            runnable.run();
    }

    public boolean hasAuthenticated() {
        return !accessTokenFile.isEmpty();
    }

    public AccessToken getAccessToken() {
        return new AccessToken(accessTokenFile.getString("access_token"), accessTokenFile.getString("access_secret"));
    }
}
