package com.app.cinx.api.dto;

import java.util.List;

public class CreateQuizLessonRequest {
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

    private Boolean isReviewAllowed;
    public Boolean getIsReviewAllowed() { return isReviewAllowed; }
    public void setIsReviewAllowed(Boolean val) { this.isReviewAllowed = val; }

    private Boolean isShowAnswersOnReview;
    public Boolean getIsShowAnswersOnReview() { return isShowAnswersOnReview; }
    public void setIsShowAnswersOnReview(Boolean val) { this.isShowAnswersOnReview = val; }

    private List<CreateQuizQuestionRequest> questions;
    public List<CreateQuizQuestionRequest> getQuestions() { return questions; }
    public void setQuestions(List<CreateQuizQuestionRequest> val) { this.questions = val; }

}