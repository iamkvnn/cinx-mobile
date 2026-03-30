package com.app.cinx.api.dto;

import java.util.List;

public class InstructorResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String name;
    public String getName() { return name; }
    public void setName(String val) { this.name = val; }

    private String email;
    public String getEmail() { return email; }
    public void setEmail(String val) { this.email = val; }

    private String bio;
    public String getBio() { return bio; }
    public void setBio(String val) { this.bio = val; }

    private String profilePictureUrl;
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String val) { this.profilePictureUrl = val; }

}