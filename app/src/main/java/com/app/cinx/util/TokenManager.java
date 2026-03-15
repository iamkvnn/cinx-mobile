package com.app.cinx.util;

/**
 * Singleton that stores JWT access/refresh tokens in memory.
 * Tokens are cleared when the process is killed (same as UserManager).
 */
public class TokenManager {

    private static TokenManager instance;

    private String accessToken;
    private String refreshToken;

    private TokenManager() {}

    public static TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    public String getAccessToken()  { return accessToken; }
    public String getRefreshToken() { return refreshToken; }

    public void saveTokens(String accessToken, String refreshToken) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
    }

    public void clear() {
        accessToken  = null;
        refreshToken = null;
    }

    public boolean hasToken() {
        return accessToken != null && !accessToken.isEmpty();
    }

    /** Returns a Bearer header value ready for use in Authorization. */
    public String getBearerToken() {
        return "Bearer " + accessToken;
    }
}
