package com.app.cinx.api.dto;

import java.util.List;

public class QuizQuestionAnalyticsResponse {
    private String questionId;
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String val) { this.questionId = val; }

    private Integer totalAttempts;
    public Integer getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(Integer val) { this.totalAttempts = val; }

    private Integer correctAttempts;
    public Integer getCorrectAttempts() { return correctAttempts; }
    public void setCorrectAttempts(Integer val) { this.correctAttempts = val; }

    private Double accuracy;
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double val) { this.accuracy = val; }

}