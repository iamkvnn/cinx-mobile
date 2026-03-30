package com.app.cinx.api.dto;

import java.util.List;

public class RegisterRequest {
    private String name;
    public String getName() { return name; }
    public void setName(String val) { this.name = val; }

    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String password;
    public String getPassword() { return password; }
    public void setPassword(String val) { this.password = val; }

    private String role;
    public String getRole() { return role; }
    public void setRole(String val) { this.role = val; }

    private String gender;
    public String getGender() { return gender; }
    public void setGender(String val) { this.gender = val; }

}