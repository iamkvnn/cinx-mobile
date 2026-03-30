package com.app.cinx.api.dto;

import java.util.List;

public class QuizQuestionResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String questionText;
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String val) { this.questionText = val; }

    private String questionType;
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String val) { this.questionType = val; }

    private Integer orderIndex;
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer val) { this.orderIndex = val; }

    private List<QuizOptionResponse> options;
    public List<QuizOptionResponse> getOptions() { return options; }
    public void setOptions(List<QuizOptionResponse> val) { this.options = val; }

}