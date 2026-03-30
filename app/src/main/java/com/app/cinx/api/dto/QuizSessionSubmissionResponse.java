package com.app.cinx.api.dto;

import java.util.List;

public class QuizSessionSubmissionResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private String submissionTime;
    public String getSubmissionTime() { return submissionTime; }
    public void setSubmissionTime(String val) { this.submissionTime = val; }

    private String quizSessionId;
    public String getQuizSessionId() { return quizSessionId; }
    public void setQuizSessionId(String val) { this.quizSessionId = val; }

    private Integer totalCorrectAnswers;
    public Integer getTotalCorrectAnswers() { return totalCorrectAnswers; }
    public void setTotalCorrectAnswers(Integer val) { this.totalCorrectAnswers = val; }

    private Double score;
    public Double getScore() { return score; }
    public void setScore(Double val) { this.score = val; }

}