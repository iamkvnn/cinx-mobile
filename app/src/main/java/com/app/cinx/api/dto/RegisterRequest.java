package com.app.cinx.api.dto;

public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String gender;

    public RegisterRequest(String name, String email, String password, String gender) {
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.gender   = gender;
    }
}
