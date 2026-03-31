package com.app.cinx.api.dto;

import java.util.List;

public class SetDailyGoalRequest {
    private Integer targetXp;
    public Integer getTargetXp() { return targetXp; }
    public void setTargetXp(Integer val) { this.targetXp = val; }

    private String goalDate;
    public String getGoalDate() { return goalDate; }
    public void setGoalDate(String val) { this.goalDate = val; }

}