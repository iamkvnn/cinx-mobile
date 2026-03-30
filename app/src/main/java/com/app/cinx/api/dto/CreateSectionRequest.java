package com.app.cinx.api.dto;

import java.util.List;

public class CreateSectionRequest {
    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private String description;
    public String getDescription() { return description; }
    public void setDescription(String val) { this.description = val; }

    private Long duration;
    public Long getDuration() { return duration; }
    public void setDuration(Long val) { this.duration = val; }

    private Integer orderIndex;
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer val) { this.orderIndex = val; }

    private List<CreateLessonRequest> lessons;
    public List<CreateLessonRequest> getLessons() { return lessons; }
    public void setLessons(List<CreateLessonRequest> val) { this.lessons = val; }

}