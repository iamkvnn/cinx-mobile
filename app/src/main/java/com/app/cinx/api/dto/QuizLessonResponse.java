package com.app.cinx.api.dto;

import java.util.List;

public class QuizLessonResponse {
    private String startTime;
    public String getStartTime() { return startTime; }
    public void setStartTime(String val) { this.startTime = val; }

    private String endTime;
    public String getEndTime() { return endTime; }
    public void setEndTime(String val) { this.endTime = val; }

    private Integer numberOfQuestionPerQuizSession;
    public Integer getNumberOfQuestionPerQuizSession() { return numberOfQuestionPerQuizSession; }
    public void setNumberOfQuestionPerQuizSession(Integer val) { this.numberOfQuestionPerQuizSession = val; }

    private Integer maxAttempt;
    public Integer getMaxAttempt() { return maxAttempt; }
    public void setMaxAttempt(Integer val) { this.maxAttempt = val; }

    private Integer duration;
    public Integer getDuration() { return duration; }
    public void setDuration(Integer val) { this.duration = val; }

    private List<QuizQuestionResponse> questions;
    public List<QuizQuestionResponse> getQuestions() { return questions; }
    public void setQuestions(List<QuizQuestionResponse> val) { this.questions = val; }

}