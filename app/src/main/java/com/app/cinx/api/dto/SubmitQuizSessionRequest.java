package com.app.cinx.api.dto;

import java.util.List;

public class SubmitQuizSessionRequest {
    private List<ChooseQuizAnswerRequest> answers;
    public List<ChooseQuizAnswerRequest> getAnswers() { return answers; }
    public void setAnswers(List<ChooseQuizAnswerRequest> val) { this.answers = val; }

}