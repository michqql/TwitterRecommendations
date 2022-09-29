package me.mpearso.twitter.gui.panels;

import me.mpearso.twitter.gui.ContentPanel;
import me.mpearso.twitter.gui.TRMainWindow;
import me.mpearso.twitter.gui.util.StringSize;
import me.mpearso.twitter.interest.InterestHandler;
import me.mpearso.twitter.interest.Recommendation;
import twitter4j.Twitter;
import twitter4j.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecommendationsPanel extends ContentPanel {

    private final Twitter api;
    private InterestHandler interestHandler;

    private List<Recommendation> recommendations = new ArrayList<>();

    // GUI Components
    private JLabel infoText1;

    public RecommendationsPanel(TRMainWindow mainWindow, Twitter api) {
        super(mainWindow);
        this.api = api;

        setBackground(TRMainWindow.BACKGROUND_COLOUR);
        setPreferredSize(new Dimension(100, 100));
        setLayout(null);
        refresh();
    }

    private void createInterestHandler(User selfUser) {
        if(interestHandler != null)
            return;

        this.interestHandler = new InterestHandler(api, selfUser, (recommendations) -> {
            RecommendationsPanel.this.recommendations = recommendations;
            refresh();
        });
    }

    public void refresh() {
        this.removeAll();

        // Info text 1
        {
            this.infoText1 = new JLabel();
            if(recommendations == null || recommendations.isEmpty()) {
                infoText1.setText("Generating recommendations...");
            } else {
                infoText1.setText("Recommendations:");
            }

            infoText1.setFont(StringSize.DEFAULT_FONT);
            Rectangle2D bounds = StringSize.getStringBounds(infoText1.getText(), infoText1.getFont());
            infoText1.setForeground(TRMainWindow.TEXT_COLOUR);
            infoText1.setBounds(TRMainWindow.WIDTH / 2 - (int) (bounds.getWidth() / 2),
                    5,
                    (int) bounds.getWidth(),
                    (int) bounds.getHeight());
            this.add(infoText1);
        }

        // Refresh button
        refresh:
        {
            // If recommendations are currently being generated, don't display this
            if(interestHandler == null || recommendations.isEmpty())
                break refresh;

            // The button to refresh
            JButton button = new JButton("Refresh");

            // Set the position
            button.setBounds(
                    TRMainWindow.WIDTH - 200,
                    5,
                    100,
                    50);

            // Set the click event
            // The recommendation list is cleared and the screen is updated.
            // This is to show the 'accounts generating...' text again to give the user feedback
            // Next, new accounts are generated and once completed, the screen is updated again
            button.addActionListener(e -> {
                RecommendationsPanel.this.recommendations = new ArrayList<>();
                refresh();

                interestHandler.process(recommendationsIn -> {
                    RecommendationsPanel.this.recommendations = recommendationsIn;
                    refresh();
                });
            });
            add(button);
        }

        // Interest search bar
        search:
        {
            // If recommendations are currently being generated, don't display this
            if(interestHandler == null || recommendations.isEmpty())
                break search;

            // Text letting the user know what the text field is for
            JLabel label = new JLabel("Search bar");
            label.setForeground(TRMainWindow.TEXT_COLOUR);

            // Set position of text
            label.setBounds(
                    10,
                    5,
                    TRMainWindow.WIDTH,
                    30);
            add(label);

            // The text field to enter the search terms
            JTextField textField = new JTextField();
            textField.setBounds(
                    10,
                    30,
                    250,
                    20);

            // Hint when hovering to inform user how to properly use
            textField.setToolTipText("Separate searches by commas (,)");

            // The screen is cleared, the search terms are separated by commas
            // and then the recommendations are refined using these new search terms
            textField.addActionListener(e -> {
                String text = textField.getText();
                String[] textSplit = text.split(",");
                List<String> interests = Arrays.asList(textSplit);
                interestHandler.setInterestsOfSelfUser(interests);

                RecommendationsPanel.this.recommendations = new ArrayList<>();
                refresh();

                interestHandler.process(recommendationsIn -> {
                    RecommendationsPanel.this.recommendations = recommendationsIn;
                    refresh();
                });
            });
            add(textField);
        }

        // Recommendations
        for(int i = 0; i < recommendations.size(); i++) {
            if(i >= 10)
                break;

            // The current recommendation and the user associated with it
            Recommendation recommendation = recommendations.get(i);
            User user = recommendation.getTwitterUser();

            // The label displaying user profile picture and name
            JLabel label = new JLabel(user.getName());
            label.setForeground(TRMainWindow.TEXT_COLOUR);

            // Get the image from the URL
            Image image = null;
            try {
                URL url = new URL(user.get400x400ProfileImageURL());
                image = ImageIO.read(url);
            } catch (IOException ignore) {}

            // Scale the image down to 50x50, then apply it to the label
            if(image != null) {
                Image scaled = image.getScaledInstance(50, 50, Image.SCALE_DEFAULT);
                label.setIcon(new ImageIcon(scaled));
            }

            // Set the position of the label, slightly to the left of center
            label.setBounds(
                    TRMainWindow.WIDTH / 2 - 10 - 300,
                    30 + 55 * i,
                    TRMainWindow.WIDTH,
                    50);
            add(label);

            // The button to open their account in Twitter
            JButton button = new JButton("Open in Twitter");
            button.setBounds(TRMainWindow.WIDTH / 2 + 10, 30 + 55 * i + 10, 200, 30);

            // Open in user's default browser upon clicking
            button.addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.twitter.com/" + user.getScreenName()));
                } catch (IOException | URISyntaxException ignore) {}
            });

            add(button);
        }

        super.repaint();
    }

    public void setUser(User user) {
        createInterestHandler(user);
    }
}
