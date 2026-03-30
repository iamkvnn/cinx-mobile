package com.app.cinx.api.dto;

import java.util.List;

public class UserNotificationResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private String message;
    public String getMessage() { return message; }
    public void setMessage(String val) { this.message = val; }

    private Boolean isRead;
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean val) { this.isRead = val; }

}