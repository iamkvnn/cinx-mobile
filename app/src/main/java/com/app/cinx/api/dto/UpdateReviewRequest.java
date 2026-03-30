package com.app.cinx.api.dto;

import java.util.List;

public class UpdateReviewRequest {
    private String content;
    public String getContent() { return content; }
    public void setContent(String val) { this.content = val; }

    private Double rating;
    public Double getRating() { return rating; }
    public void setRating(Double val) { this.rating = val; }

}