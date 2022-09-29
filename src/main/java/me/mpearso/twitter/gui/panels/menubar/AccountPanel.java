package me.mpearso.twitter.gui.panels.menubar;

import me.mpearso.twitter.gui.ContentPanel;
import me.mpearso.twitter.gui.TRMainWindow;

import javax.swing.*;
import java.awt.*;

public class AccountPanel extends ContentPanel {

    public AccountPanel(TRMainWindow mainWindow) {
        super(mainWindow);

        setBackground(TRMainWindow.BACKGROUND_COLOUR);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Account");
        label.setForeground(TRMainWindow.TEXT_COLOUR);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label);
    }

    @Override
    public void refresh() {

    }
}
