package com.app.cinx.api.dto;

public class UserProfileDto {
    private String userId;
    private String name;
    private String email;
    private String gender;
    private String avatarUrl;

    public String getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getGender()   { return gender; }
    public String getAvatarUrl() { return avatarUrl; }
}
