package com.app.cinx.api.dto;

public class UpdateUserDto {
    private String name;
    private String gender;

    public UpdateUserDto(String name, String gender) {
        this.name   = name;
        this.gender = gender;
    }
}
