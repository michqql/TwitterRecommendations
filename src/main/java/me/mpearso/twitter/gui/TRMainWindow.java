package me.mpearso.twitter.gui;

import me.mpearso.twitter.gui.panels.LoginPanel;
import me.mpearso.twitter.gui.panels.MenuBarPanel;
import me.mpearso.twitter.gui.panels.RecommendationsPanel;
import me.mpearso.twitter.interest.InterestHandler;
import me.mpearso.twitter.login.LoginHandler;
import twitter4j.Twitter;

import javax.swing.*;
import java.awt.*;

public class TRMainWindow {

    public enum ContentPanel {
        LOGIN, RECOMMENDATIONS, SETTINGS
    }

    public static final Color BACKGROUND_COLOUR = new Color(0x23272A);
    public static final Color CONTENT_BACKGROUND_COLOUR = new Color(44, 47, 51);
    public static final Color TEXT_COLOUR = Color.WHITE;
    public static final Color ERROR_TEXT_COLOUR = Color.RED;

    public static final int WIDTH = 1280, HEIGHT = 720;

    // The java window
    private final JFrame window;

    // Variables
    private final Twitter api;
    private LoginHandler loginHandler;
    private InterestHandler interestHandler;

    // Panels
    private final MenuBarPanel menuBarPanel;
    private final LoginPanel loginPanel;
    private final RecommendationsPanel recommendationsPanel;

    public TRMainWindow(Twitter api, LoginHandler loginHandler) throws HeadlessException {
        this.api = api;
        this.loginHandler = loginHandler;
        this.window = new JFrame(); // Creates a new GUI frame

        // Exits the program when the 'X' button is pressed on the window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setSize(WIDTH, HEIGHT); // Sets the width and height of frame
        window.setTitle("Twitter Recommendations"); // Sets title of frame
        window.setResizable(false); // Prevents frame from being resized, as this will mess up component sizes
        window.setLayout(new BorderLayout(10, 10));
        window.setLocationRelativeTo(null);
        window.getContentPane().setBackground(CONTENT_BACKGROUND_COLOUR);

        // Create panels
        this.menuBarPanel = new MenuBarPanel(this);
        this.recommendationsPanel = new RecommendationsPanel(this, api);
        this.loginPanel = new LoginPanel(this, loginHandler);

        // Set the current content panel + the menu bar
        window.add(menuBarPanel, BorderLayout.NORTH);
        if(loginHandler.isAuthenticated()) {
            recommendationsPanel.setUser(loginHandler.getUser());
            setContentPanel(ContentPanel.RECOMMENDATIONS);
        } else {
            setContentPanel(ContentPanel.LOGIN);
        }

        window.setVisible(true); // Makes the frame visible
    }

    public void setContentPanel(ContentPanel type) {
        loginPanel.setVisible(false);
        recommendationsPanel.setVisible(false);

        switch (type) {
            case LOGIN:
                window.add(loginPanel, BorderLayout.CENTER);
                loginPanel.setVisible(true);
                break;

            case RECOMMENDATIONS:
                recommendationsPanel.setUser(loginHandler.getUser());
                window.add(recommendationsPanel, BorderLayout.CENTER);
                recommendationsPanel.setVisible(true);
                break;
        }
    }
}
