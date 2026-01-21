package com.app.cinx.model;

public class Testimonial {
    private int id;
    private String text;
    private String userName;
    private String avatarUrl;

    public Testimonial(int id, String text, String userName, String avatarUrl) {
        this.id = id;
        this.text = text;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
