package me.mpearso.twitter.gui.panels;

import me.mpearso.twitter.gui.TRMainWindow;
import me.mpearso.twitter.gui.panels.menubar.AccountPanel;

import javax.swing.*;
import java.awt.*;

public class MenuBarPanel extends JPanel {

    public MenuBarPanel(TRMainWindow mainWindow) {
        setBackground(TRMainWindow.BACKGROUND_COLOUR);
        setPreferredSize(new Dimension(100, 100));
        setLayout(new BorderLayout());

        add(new AccountPanel(mainWindow), BorderLayout.CENTER);
        add(new SettingsPanel(), BorderLayout.EAST);
    }

    static class SettingsPanel extends JPanel {

        public SettingsPanel() {
            setBackground(TRMainWindow.BACKGROUND_COLOUR);
            setPreferredSize(new Dimension(200, 100));
            setLayout(new BorderLayout());

            JLabel label = new JLabel("Settings");
            label.setForeground(TRMainWindow.TEXT_COLOUR);
            label.setVerticalAlignment(JLabel.CENTER);
            label.setHorizontalAlignment(JLabel.CENTER);
            add(label);
        }
    }
}
