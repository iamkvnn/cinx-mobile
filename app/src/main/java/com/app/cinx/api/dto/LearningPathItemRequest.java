package com.app.cinx.api.dto;

import java.util.List;

public class LearningPathItemRequest {
    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private String lessonId;
    public String getLessonId() { return lessonId; }
    public void setLessonId(String val) { this.lessonId = val; }

    private Integer orderIndex;
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer val) { this.orderIndex = val; }

    private Boolean isSuggested;
    public Boolean getIsSuggested() { return isSuggested; }
    public void setIsSuggested(Boolean val) { this.isSuggested = val; }

}