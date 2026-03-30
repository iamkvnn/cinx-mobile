package com.app.cinx.api.dto;

import java.util.List;

public class AuthRequestDto {
    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String password;
    public String getPassword() { return password; }
    public void setPassword(String val) { this.password = val; }

}