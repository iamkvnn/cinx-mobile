package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateQuizLessonRequest {
    @SerializedName("startTime")
    private String startTime;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @SerializedName("endTime")
    private String endTime;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @SerializedName("numberOfQuestionPerQuizSession")
    private Integer numberOfQuestionPerQuizSession;

    public Integer getNumberOfQuestionPerQuizSession() {
        return numberOfQuestionPerQuizSession;
    }

    public void setNumberOfQuestionPerQuizSession(Integer numberOfQuestionPerQuizSession) {
        this.numberOfQuestionPerQuizSession = numberOfQuestionPerQuizSession;
    }

    @SerializedName("maxAttempt")
    private Integer maxAttempt;

    public Integer getMaxAttempt() {
        return maxAttempt;
    }

    public void setMaxAttempt(Integer maxAttempt) {
        this.maxAttempt = maxAttempt;
    }

    @SerializedName("duration")
    private Integer duration;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @SerializedName("questions")
    private List<CreateQuizQuestionRequest> questions;

    public List<CreateQuizQuestionRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<CreateQuizQuestionRequest> questions) {
        this.questions = questions;
    }

    // mock field preserved for ui consistency
}
