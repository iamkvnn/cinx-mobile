package com.app.cinx.api.dto;

import java.util.List;

public class DailyGoalResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private Integer targetXp;
    public Integer getTargetXp() { return targetXp; }
    public void setTargetXp(Integer val) { this.targetXp = val; }

    private Integer currentXp;
    public Integer getCurrentXp() { return currentXp; }
    public void setCurrentXp(Integer val) { this.currentXp = val; }

    private String goalDate;
    public String getGoalDate() { return goalDate; }
    public void setGoalDate(String val) { this.goalDate = val; }

    private Boolean isCompleted;
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean val) { this.isCompleted = val; }

}