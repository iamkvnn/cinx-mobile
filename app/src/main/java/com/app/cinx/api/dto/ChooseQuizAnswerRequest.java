package com.app.cinx.api.dto;

import java.util.List;

public class ChooseQuizAnswerRequest {
    private String questionId;
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String val) { this.questionId = val; }

    private String userAnswer;
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String val) { this.userAnswer = val; }

}