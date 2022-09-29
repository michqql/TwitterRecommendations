package me.mpearso.twitter.gui;

import javax.swing.*;

public abstract class ContentPanel extends JPanel {

    protected TRMainWindow mainWindow;

    public ContentPanel(TRMainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public abstract void refresh();
}
