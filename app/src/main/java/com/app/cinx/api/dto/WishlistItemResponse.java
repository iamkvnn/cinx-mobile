package com.app.cinx.api.dto;

import java.util.List;

public class WishlistItemResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

}