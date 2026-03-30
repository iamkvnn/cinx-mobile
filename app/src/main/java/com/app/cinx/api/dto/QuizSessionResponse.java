package com.app.cinx.api.dto;

import java.util.List;

public class QuizSessionResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String quizLessonId;
    public String getQuizLessonId() { return quizLessonId; }
    public void setQuizLessonId(String val) { this.quizLessonId = val; }

    private String startTime;
    public String getStartTime() { return startTime; }
    public void setStartTime(String val) { this.startTime = val; }

    private String endTime;
    public String getEndTime() { return endTime; }
    public void setEndTime(String val) { this.endTime = val; }

    private String status;
    public String getStatus() { return status; }
    public void setStatus(String val) { this.status = val; }

    private QuizSessionSubmissionResponse quizSessionSubmission;
    public QuizSessionSubmissionResponse getQuizSessionSubmission() { return quizSessionSubmission; }
    public void setQuizSessionSubmission(QuizSessionSubmissionResponse val) { this.quizSessionSubmission = val; }

}