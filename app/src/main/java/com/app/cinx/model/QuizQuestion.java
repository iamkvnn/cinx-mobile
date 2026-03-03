package com.app.cinx.model;

import java.util.List;

/**
 * Represents a single quiz question with multiple choice options.
 */
public class QuizQuestion {
    private final int id;
    private final String questionText;
    private final List<QuizOption> options;

    public QuizQuestion(int id, String questionText, List<QuizOption> options) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
    }

    public int getId() { return id; }
    public String getQuestionText() { return questionText; }
    public List<QuizOption> getOptions() { return options; }
}
