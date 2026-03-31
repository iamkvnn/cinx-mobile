package com.app.cinx.api.dto;

import java.util.List;

public class LearningPathRequest {
    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private List<LearningPathItemRequest> items;
    public List<LearningPathItemRequest> getItems() { return items; }
    public void setItems(List<LearningPathItemRequest> val) { this.items = val; }

}