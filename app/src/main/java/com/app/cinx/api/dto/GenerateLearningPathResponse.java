package com.app.cinx.api.dto;

import java.util.List;

public class GenerateLearningPathResponse {
    private String pathName;
    public String getPathName() { return pathName; }
    public void setPathName(String val) { this.pathName = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private List<GenerateLearningPathItem> items;
    public List<GenerateLearningPathItem> getItems() { return items; }
    public void setItems(List<GenerateLearningPathItem> val) { this.items = val; }

}