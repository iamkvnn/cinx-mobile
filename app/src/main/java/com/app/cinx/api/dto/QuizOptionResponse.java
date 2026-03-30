package com.app.cinx.api.dto;

import java.util.List;

public class QuizOptionResponse {
    private String optionText;
    public String getOptionText() { return optionText; }
    public void setOptionText(String val) { this.optionText = val; }

    private Boolean isCorrect;
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean val) { this.isCorrect = val; }

    private Integer optionOrder;
    public Integer getOptionOrder() { return optionOrder; }
    public void setOptionOrder(Integer val) { this.optionOrder = val; }

}