package com.app.cinx.api.dto;

import java.util.List;

public class CheckEnrollmentStatus {
    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private Boolean isEnrolled;
    public Boolean getIsEnrolled() { return isEnrolled; }
    public void setIsEnrolled(Boolean val) { this.isEnrolled = val; }

}