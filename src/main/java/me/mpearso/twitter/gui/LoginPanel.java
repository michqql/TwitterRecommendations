package me.mpearso.twitter.gui;

import me.mpearso.twitter.account.LoginHandler;

import javax.swing.*;

public class LoginPanel extends JPanel {

    private final LoginHandler loginHandler;

    public LoginPanel(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;

        this.setLayout(null);
        //this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        refresh();
    }

    public void refresh() {
        boolean logged = loginHandler.isAuthenticated();

        JLabel loggedIn = new JLabel(logged ? "Logged in" : "Not logged in");
        loggedIn.setBounds(10, 20, 80, 25);
        this.add(loggedIn);

        //if(!logged)

        JButton button = new JButton("Authenticate");
        button.addActionListener(e -> {
            loginHandler.openURLInDefaultBrowser();
        });
        this.add(button);
    }
}
