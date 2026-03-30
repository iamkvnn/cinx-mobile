package com.app.cinx.api.dto;

import java.util.List;

public class TokenResponseDto {
    private String accessToken;
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String val) { this.accessToken = val; }

    private String refreshToken;
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String val) { this.refreshToken = val; }

}