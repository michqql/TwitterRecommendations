package me.mpearso.twitter.gui;

import me.mpearso.twitter.login.LoginHandler;

public class GUI {

    private final LoginHandler loginHandler;

    public GUI(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;

        //new Window(loginHandler);
    }
}
