package com.app.cinx.api.dto;

import java.util.List;

public class CartItemResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private CourseResponse course;
    public CourseResponse getCourse() { return course; }
    public void setCourse(CourseResponse val) { this.course = val; }

    private Boolean isSelected = false;
    public Boolean isSelected() { return isSelected != null && isSelected; }
    public void setSelected(Boolean val) { this.isSelected = val; }

    public String getTitle() { return course != null ? course.getTitle() : ""; }
    public String getInstructor() { return course != null && course.getInstructor() != null ? course.getInstructor().getName() : (course != null ? course.getDescription() : ""); }
    public long getSalePrice() { return course != null && course.getDiscountedPrice() != null ? course.getDiscountedPrice() : (course != null && course.getPrice() != null ? course.getPrice() : 0L); }
    public long getOriginalPrice() { return course != null && course.getPrice() != null ? course.getPrice() : 0L; }
    public String getImageUrl() { return "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400"; }
    public String getCategory() { return course != null ? course.getCategory() : ""; }

}