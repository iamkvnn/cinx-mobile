package com.app.cinx.api.dto;

import java.util.List;

public class UserStreakResponse {
    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private Integer currentStreak;
    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer val) { this.currentStreak = val; }

    private Integer highestStreak;
    public Integer getHighestStreak() { return highestStreak; }
    public void setHighestStreak(Integer val) { this.highestStreak = val; }

    private String lastActivityDate;
    public String getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(String val) { this.lastActivityDate = val; }

}