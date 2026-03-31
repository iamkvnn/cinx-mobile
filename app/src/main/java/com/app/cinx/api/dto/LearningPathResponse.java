package com.app.cinx.api.dto;

import java.util.List;

public class LearningPathResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private String status;
    public String getStatus() { return status; }
    public void setStatus(String val) { this.status = val; }

    private Double currentProgress;
    public Double getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(Double val) { this.currentProgress = val; }

    private Integer totalItems;
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer val) { this.totalItems = val; }

    private Integer completedItems;
    public Integer getCompletedItems() { return completedItems; }
    public void setCompletedItems(Integer val) { this.completedItems = val; }

    private List<LearningPathItemResponse> items;
    public List<LearningPathItemResponse> getItems() { return items; }
    public void setItems(List<LearningPathItemResponse> val) { this.items = val; }

}