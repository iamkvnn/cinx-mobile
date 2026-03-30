package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SubmitQuizSessionRequest {
    @SerializedName("answers")
    private List<ChooseQuizAnswerRequest> answers;

    public List<ChooseQuizAnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<ChooseQuizAnswerRequest> answers) {
        this.answers = answers;
    }

    // mock field preserved for ui consistency
}
