package me.mpearso.twitter.gui;

import me.mpearso.twitter.gui.panels.MenuBarPanel;

import javax.swing.*;
import java.awt.*;

public class TRMainWindow {

    public static final Color DARK_BACKGROUND_COLOUR = new Color(0x23272A);
    public static final Color DARK_CONTENT_BACKGROUND_COLOUR = new Color(44, 47, 51);
    public static final Color DARK_TEXT_COLOUR       = Color.WHITE;

    // The java swing window
    private final JFrame frame;

    public TRMainWindow() throws HeadlessException {
        this.frame = new JFrame(); // Creates a new GUI frame

        // Exits the program when the 'X' button is pressed on the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1280, 720); // Sets the width and height of frame
        frame.setTitle("Twitter Recommendations"); // Sets title of frame
        frame.setResizable(false); // Prevents frame from being resized
        frame.setLayout(new BorderLayout(10, 10));
        frame.setLocationRelativeTo(null);

//        JPanel panel = new JPanel();
//        panel.setBackground(Color.BLACK);
//        panel.setBounds(640, 0, 624, 680);
//        panel.setLayout(new BorderLayout());
//        frame.add(panel);
//
//        JLabel label = new JLabel();
//        label.setText("Hello");
//        label.setForeground(DARK_TEXT_COLOUR);
//        label.setBorder(BorderFactory.createLineBorder(new Color(0x7289DA), 5));
//        label.setVerticalAlignment(JLabel.CENTER);
//        label.setHorizontalAlignment(JLabel.CENTER);
//        panel.add(label);

        //frame.getContentPane().setBackground(DARK_BACKGROUND_COLOUR);

        frame.add(new MenuBarPanel(), BorderLayout.NORTH);
        frame.setVisible(true); // Makes the frame visible
    }
}
