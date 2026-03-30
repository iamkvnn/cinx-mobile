package com.app.cinx.api.dto;

import java.util.List;

public class VideoLessonTrackingHistoryResponse {
    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String videoLessonId;
    public String getVideoLessonId() { return videoLessonId; }
    public void setVideoLessonId(String val) { this.videoLessonId = val; }

    private Integer currentPosition;
    public Integer getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Integer val) { this.currentPosition = val; }

    private String lastTrackingTime;
    public String getLastTrackingTime() { return lastTrackingTime; }
    public void setLastTrackingTime(String val) { this.lastTrackingTime = val; }

}