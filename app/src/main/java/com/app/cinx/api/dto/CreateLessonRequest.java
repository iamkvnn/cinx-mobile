package com.app.cinx.api.dto;

import java.util.List;

public class CreateLessonRequest {
    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private Long duration;
    public Long getDuration() { return duration; }
    public void setDuration(Long val) { this.duration = val; }

    private Integer orderIndex;
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer val) { this.orderIndex = val; }

    private String lessonType;
    public String getLessonType() { return lessonType; }
    public void setLessonType(String val) { this.lessonType = val; }

}