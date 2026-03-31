package com.app.cinx.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.cinx.CinxApp;

/**
 * Singleton that stores JWT access/refresh tokens in SharedPreferences.
 */
public class TokenManager {

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private static final String PREF_NAME = "token_prefs";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private TokenManager() {
        prefs = CinxApp.getInstance().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public String getAccessToken() { 
        return prefs.getString(KEY_ACCESS_TOKEN, null); 
    }
    
    public String getRefreshToken() { 
        return prefs.getString(KEY_REFRESH_TOKEN, null); 
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply();
    }

    public void clear() {
        prefs.edit().clear().apply();
    }

    public boolean hasToken() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }

    /** Returns a Bearer header value ready for use in Authorization. */
    public String getBearerToken() {
        String token = getAccessToken();
        return token != null ? "Bearer " + token : null;
    }
}
