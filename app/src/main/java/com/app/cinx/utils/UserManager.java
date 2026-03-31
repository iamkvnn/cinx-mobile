package com.app.cinx.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.cinx.CinxApp;

public class UserManager {
    private static UserManager instance;
    private final SharedPreferences prefs;

    private static final String PREF_NAME = "user_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_AVATAR = "user_avatar";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_USER_XP = "user_xp";

    private UserManager() {
        prefs = CinxApp.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void login(String email) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .apply();
    }
    
    public void saveUserInfo(String userId, String email, String name, String avatarUrl, String role) {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .putString(KEY_USER_AVATAR, avatarUrl)
            .putString(KEY_USER_ROLE, role)
            .apply();
    }

    public void logout() {
        prefs.edit().clear().apply();
        // Also clear tokens
        TokenManager.getInstance().clear();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

    public String getAvatarUrl() {
        return prefs.getString(KEY_USER_AVATAR, null);
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    public void setUserEmail(String email) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply();
    }
    
    public void setUserName(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public int getUserXp() {
        return prefs.getInt(KEY_USER_XP, 0);
    }

    public void setUserXp(int xp) {
        prefs.edit().putInt(KEY_USER_XP, xp).apply();
    }
}
