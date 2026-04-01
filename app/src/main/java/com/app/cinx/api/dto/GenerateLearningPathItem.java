package com.app.cinx.api.dto;

import java.util.List;

public class GenerateLearningPathItem {
    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private String lessonId;
    public String getLessonId() { return lessonId; }
    public void setLessonId(String val) { this.lessonId = val; }

    private String reason;
    public String getReason() { return reason; }
    public void setReason(String val) { this.reason = val; }

}