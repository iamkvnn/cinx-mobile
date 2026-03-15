package com.app.cinx.api.dto;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email    = email;
        this.password = password;
    }
}
