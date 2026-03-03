package com.app.cinx.model;

/**
 * Represents a single option in a quiz question.
 */
public class QuizOption {
    private final String text;
    private final boolean correct;

    public QuizOption(String text, boolean correct) {
        this.text = text;
        this.correct = correct;
    }

    public String getText() { return text; }
    public boolean isCorrect() { return correct; }
}
