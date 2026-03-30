package com.app.cinx.api.dto;

import java.util.List;

public class CartItemResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private CourseResponse course;
    public CourseResponse getCourse() { return course; }
    public void setCourse(CourseResponse val) { this.course = val; }

}