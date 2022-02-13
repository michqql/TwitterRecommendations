package me.mpearso.twitter.login;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.mpearso.twitter.io.impl.JsonFile;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LoginHandler {

    private final static String ACCESS_TOKEN_KEY = "access_token";
    private final static String ACCESS_SECRET_KEY = "access_secret";

    // The twitter instance
    private final Twitter twitter;

    // The file that stores users token key and secret for this app
    private final JsonFile accessTokenFile;

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
        this.accessTokenFile = new JsonFile("data/tokens.json");

        // If the user has already authenticated,
        // we don't need to get them to do it again
        try {
            if(isAuthenticationDataSaved()) {
                // We can assert that the json object is in a standard format
                // at this point, as it passed checks in isAuthenticationDataSaved
                // and therefore do not need to validate this again here
                JsonObject object = accessTokenFile.getAsJsonObject();

                this.accessToken = new AccessToken(
                        object.get(ACCESS_TOKEN_KEY).getAsString(),
                        object.get(ACCESS_SECRET_KEY).getAsString()
                );

                // Pass in the access token to the API to authenticate
                twitter.setOAuthAccessToken(accessToken);
                onAuthentication();
            } else {
                this.requestToken = twitter.getOAuthRequestToken();
            }
        } catch(TwitterException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Checks if the user has been authenticated this session
     * @return {@code true} if authenticated
     */
    public final boolean isAuthenticated() {
        return accessToken != null;
    }

    /**
     * @return {@code true} if the user has previously authenticated, {@code false} otherwise
     */
    public final boolean isAuthenticationDataSaved() {
        JsonElement element = accessTokenFile.getElement();
        if(element == null || !element.isJsonObject())
            return false;

        JsonObject object = element.getAsJsonObject();
        return object.has(ACCESS_TOKEN_KEY) && object.has(ACCESS_SECRET_KEY);
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
        if(isAuthenticated() || isAuthenticationDataSaved())
            return;

        // On some computers, this may not be supported
        // therefore we must validate that it is
        if(!Desktop.isDesktopSupported()) {
            // Throw an error to let the rest of the program know what
            // has happened and handle accordingly
            throw new UnsupportedOperationException("Desktop API not supported on this machine");
        }

        try {
            // Attempts to open the users default browser,
            // and set the page to the twitter authentication page
            Desktop.getDesktop().browse(new URI(requestToken.getAuthenticationURL()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attempts to authenticate using the entered pin
     * @param pin authentication pin
     * @return {@code true} if authentication was successful
     */
    public final boolean setPin(String pin) {
        try {
            // Attempts to get the token for current session
            if(pin == null || pin.isEmpty())
                accessToken = twitter.getOAuthAccessToken(requestToken);
            else
                accessToken = twitter.getOAuthAccessToken(requestToken, pin);

            // At this point, if no exception has been thrown
            // we can assume that authentication has been successful
            // and therefore can save the tokens to our file
            JsonElement element = accessTokenFile.getElement();
            JsonObject object;
            if(element.isJsonObject()) {
                object = element.getAsJsonObject();
            } else {
                object = new JsonObject();
                accessTokenFile.setElement(object);
            }

            // Set the appropriate data and save
            object.addProperty("name", accessToken.getScreenName());
            object.addProperty("uuid", accessToken.getUserId());
            object.addProperty(ACCESS_TOKEN_KEY, accessToken.getToken());
            object.addProperty(ACCESS_SECRET_KEY, accessToken.getTokenSecret());
            accessTokenFile.save();

            onAuthentication();
            return true;
        } catch (TwitterException e) {

            /* TODO: Don't exit here, we want to handle this exception
            *   and let the user know that the pin they entered was incorrect */
            // An authentication exception has been thrown in the process
            // This is a fatal error, quit the program.
            e.printStackTrace();
            System.exit(1);
            return false;
        }
    }

    /**
     * Sets the runnable that runs when authenticated successfully
     * @param runnable to run
     */
    public void setAuthenticationRunnable(Runnable runnable) {
        this.runnable = runnable;
        onAuthentication();
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    // Private method that will run the authentication runnable
    // and ensure that it can only be run a single time
    private void onAuthentication() {
        if(runnable != null && isAuthenticated()) {
            runnable.run();
            runnable = null;
        }
    }
}
