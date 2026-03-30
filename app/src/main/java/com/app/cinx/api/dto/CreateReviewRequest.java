package com.app.cinx.api.dto;

import java.util.List;

public class CreateReviewRequest {
    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private String content;
    public String getContent() { return content; }
    public void setContent(String val) { this.content = val; }

    private Double rating;
    public Double getRating() { return rating; }
    public void setRating(Double val) { this.rating = val; }

}