package com.app.cinx.api.dto;

import java.util.List;

public class AddToCartRequest {
    private String courseId;

    public AddToCartRequest() {}

    public AddToCartRequest(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

}