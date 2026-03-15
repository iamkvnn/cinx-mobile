package com.app.cinx.api.dto;

/** A single item in the server cart response. */
public class CartItemDto {
    private String    id;
    private CourseDto course;

    public String    getId()     { return id; }
    public CourseDto getCourse() { return course; }
}
