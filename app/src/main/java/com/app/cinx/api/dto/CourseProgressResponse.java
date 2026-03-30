package com.app.cinx.api.dto;

import java.util.List;

public class CourseProgressResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private Boolean isCompleted;
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean val) { this.isCompleted = val; }

    private Boolean isPassed;
    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean val) { this.isPassed = val; }

    private Double avgScore;
    public Double getAvgScore() { return avgScore; }
    public void setAvgScore(Double val) { this.avgScore = val; }

    private Integer totalItems;
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer val) { this.totalItems = val; }

    private Integer completedItems;
    public Integer getCompletedItems() { return completedItems; }
    public void setCompletedItems(Integer val) { this.completedItems = val; }

    private String completionTime;
    public String getCompletionTime() { return completionTime; }
    public void setCompletionTime(String val) { this.completionTime = val; }

}