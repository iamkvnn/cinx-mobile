package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;

public class EnrollmentResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("course")
    private CourseResponse course;

    @SerializedName("progress")
    private Double progress;

    @SerializedName("enrolledAt")
    private String enrolledAt;

    @SerializedName("isCompleted")
    private Boolean isCompleted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CourseResponse getCourse() {
        return course;
    }

    public void setCourse(CourseResponse course) {
        this.course = course;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(String enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
}

