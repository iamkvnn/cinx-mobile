package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TrackingVideoLessonRequest {
    @SerializedName("videoLessonId")
    private String videoLessonId;

    public String getVideoLessonId() {
        return videoLessonId;
    }

    public void setVideoLessonId(String videoLessonId) {
        this.videoLessonId = videoLessonId;
    }

    @SerializedName("currentPosition")
    private Integer currentPosition;

    public Integer getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Integer currentPosition) {
        this.currentPosition = currentPosition;
    }

    // mock field preserved for ui consistency
}
