package me.mpearso.twitter.gui.panels;

import me.mpearso.twitter.gui.TRMainWindow;

import javax.swing.*;
import java.awt.*;

public class MenuBarPanel extends JPanel {

    public MenuBarPanel() {
        setBackground(TRMainWindow.DARK_BACKGROUND_COLOUR);
        setPreferredSize(new Dimension(100, 100));
        setLayout(new BorderLayout());

        add(new AccountPanel(), BorderLayout.CENTER);
        add(new SettingsPanel(), BorderLayout.EAST);
    }

    static class AccountPanel extends JPanel {

        public AccountPanel() {
            setBackground(TRMainWindow.DARK_BACKGROUND_COLOUR);
            setLayout(new BorderLayout());

            JLabel label = new JLabel("Account");
            label.setForeground(TRMainWindow.DARK_TEXT_COLOUR);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            add(label);
        }
    }

    static class SettingsPanel extends JPanel {

        public SettingsPanel() {
            setBackground(TRMainWindow.DARK_BACKGROUND_COLOUR);
            setPreferredSize(new Dimension(200, 100));
            setLayout(new BorderLayout());

            JLabel label = new JLabel("Settings");
            label.setForeground(TRMainWindow.DARK_TEXT_COLOUR);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            add(label);
        }
    }
}
