package com.app.cinx.api.dto;

import java.util.List;

public class ReviewResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private String content;
    public String getContent() { return content; }
    public void setContent(String val) { this.content = val; }

    private Double rating;
    public Double getRating() { return rating; }
    public void setRating(Double val) { this.rating = val; }

    private List<ReviewReportResponse> reports;
    public List<ReviewReportResponse> getReports() { return reports; }
    public void setReports(List<ReviewReportResponse> val) { this.reports = val; }

    private List<ReviewReactionResponse> reactions;
    public List<ReviewReactionResponse> getReactions() { return reactions; }
    public void setReactions(List<ReviewReactionResponse> val) { this.reactions = val; }

}