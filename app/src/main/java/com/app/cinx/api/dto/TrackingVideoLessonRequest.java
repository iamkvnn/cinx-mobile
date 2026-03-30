package com.app.cinx.api.dto;

import java.util.List;

public class TrackingVideoLessonRequest {
    private String videoLessonId;
    public String getVideoLessonId() { return videoLessonId; }
    public void setVideoLessonId(String val) { this.videoLessonId = val; }

    private Integer currentPosition;
    public Integer getCurrentPosition() { return currentPosition; }
    public void setCurrentPosition(Integer val) { this.currentPosition = val; }

}