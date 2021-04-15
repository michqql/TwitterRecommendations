package me.mpearso.twitter.account;

import me.mpearso.twitter.data.text.KeyValueTextFile;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

public class LoginHandler {

    private final static String ACCESS_TOKEN_KEY = "access_token", ACCESS_SECRET_KEY = "access_secret";

    // The twitter instance
    private final Twitter twitter;

    // The file that stores users token key and secret for this app
    private final KeyValueTextFile accessTokenFile;

    // Run method that is called when authentication is successful
    private Runnable runnable;

    // Authentication
    private RequestToken requestToken;
    private AccessToken accessToken = null;

    /**
     * Main constructor method
     * Automatically calls the authentication login method
     *
     * Opens the user data file
     * Checks if the file contains data (user access token and secret)
     *
     * @param twitter instance
     */
    public LoginHandler(final Twitter twitter) {
        this.twitter = twitter;

        // Opens the token file
        this.accessTokenFile = new KeyValueTextFile("data", "tokens", ":");

        // If the user has already authenticated, we don't need to get them to do it again
        try {
            if(isAuthenticationDataSaved()) {
                this.accessToken = new AccessToken(
                        accessTokenFile.getString(ACCESS_TOKEN_KEY),
                        accessTokenFile.getString(ACCESS_SECRET_KEY)
                );
            } else {
                this.requestToken = twitter.getOAuthRequestToken();
            }
        } catch(TwitterException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * @return {@code true} if the user has previously authenticated, {@code false} otherwise
     */
    public final boolean isAuthenticationDataSaved() {
        return accessTokenFile.contains(ACCESS_TOKEN_KEY) && accessTokenFile.contains(ACCESS_SECRET_KEY);
    }

    /**
     * Opens the authentication URL in the users default browser
     *
     * Throws IOException and URISyntaxException, which are both caught within this method
     * Will not do anything if the user has already authenticated or an exception is caught
     */
    public final void openURLInDefaultBrowser() {
        // If the user has already authenticated,
        // we don't need to get the user to do this
        // again, and so can return
        if(isAuthenticationDataSaved())
            return;

        try {
            // Attempts to open the users default browser, and set the page
            // to the twitter authentication page
            Desktop.getDesktop().browse(new URI(requestToken.getAuthenticationURL()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public final void setPin(String pin) {
        if(!pin.isEmpty()) {
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                accessTokenFile.put(ACCESS_TOKEN_KEY, accessToken.getToken());
                accessTokenFile.put(ACCESS_SECRET_KEY, accessToken.getTokenSecret());
            } catch(TwitterException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void onAuthentication(Runnable run) {
        this.runnable = run;
        if(!accessTokenFile.isEmpty())
            runnable.run();
    }

    public AccessToken getAccessToken() {
        return null;
    }
}
