package com.app.cinx.util;

public class UserManager {
    private static UserManager instance;
    private boolean isLoggedIn = false;
    private String userEmail;

    private UserManager() {}

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void login(String email) {
        this.isLoggedIn = true;
        this.userEmail = email;
    }

    public void logout() {
        this.isLoggedIn = false;
        this.userEmail = null;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }
}
