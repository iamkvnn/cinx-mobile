package com.app.cinx.api.dto;

import java.util.List;

public class LearningItemProgressResponse {
    private String itemId;
    public String getItemId() { return itemId; }
    public void setItemId(String val) { this.itemId = val; }

    private Boolean isCompleted;
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean val) { this.isCompleted = val; }

    private Boolean isPassed;
    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean val) { this.isPassed = val; }

    private Double score;
    public Double getScore() { return score; }
    public void setScore(Double val) { this.score = val; }

}