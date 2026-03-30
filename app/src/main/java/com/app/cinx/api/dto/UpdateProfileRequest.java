package com.app.cinx.api.dto;

import java.util.List;

public class UpdateProfileRequest {
    private String name;
    public String getName() { return name; }
    public void setName(String val) { this.name = val; }

    private String gender;
    public String getGender() { return gender; }
    public void setGender(String val) { this.gender = val; }

}