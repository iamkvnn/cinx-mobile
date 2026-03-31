package com.app.cinx.api.dto;

import java.util.List;

public class QuizSessionQuestionResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String quizSessionId;
    public String getQuizSessionId() { return quizSessionId; }
    public void setQuizSessionId(String val) { this.quizSessionId = val; }

    private String questionId;
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String val) { this.questionId = val; }

    private String questionType;
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String val) { this.questionType = val; }

    private Integer questionOrder;
    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer val) { this.questionOrder = val; }

    private String userAnswer;
    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String val) { this.userAnswer = val; }

    private String correctAnswer;
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String val) { this.correctAnswer = val; }

    private Boolean isCorrect;
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean val) { this.isCorrect = val; }

    private Integer score;
    public Integer getScore() { return score; }
    public void setScore(Integer val) { this.score = val; }

}