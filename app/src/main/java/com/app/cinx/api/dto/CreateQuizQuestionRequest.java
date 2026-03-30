package com.app.cinx.api.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateQuizQuestionRequest {
    @SerializedName("questionText")
    private String questionText;

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @SerializedName("questionType")
    private String questionType;

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    @SerializedName("orderIndex")
    private Integer orderIndex;

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @SerializedName("options")
    private List<CreateQuizOptionRequest> options;

    public List<CreateQuizOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<CreateQuizOptionRequest> options) {
        this.options = options;
    }

    // mock field preserved for ui consistency
}
