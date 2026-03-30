package com.app.cinx.api.dto;

import java.util.List;

public class UserDto {
    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String name;
    public String getName() { return name; }
    public void setName(String val) { this.name = val; }

    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String role;
    public String getRole() { return role; }
    public void setRole(String val) { this.role = val; }

    private String gender;
    public String getGender() { return gender; }
    public void setGender(String val) { this.gender = val; }

    private String avatarUrl;
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String val) { this.avatarUrl = val; }

}