package com.app.cinx.api.dto;

import java.util.List;

public class LessonResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

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

}