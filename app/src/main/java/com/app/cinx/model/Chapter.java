package com.app.cinx.model;

import java.util.List;

/**
 * Represents a course chapter which groups multiple lessons.
 */
public class Chapter {
    private final int number;
    private final String title;
    private final List<Lesson> lessons;

    public Chapter(int number, String title, List<Lesson> lessons) {
        this.number = number;
        this.title = title;
        this.lessons = lessons;
    }

    public int getNumber() { return number; }
    public String getTitle() { return title; }
    public List<Lesson> getLessons() { return lessons; }

    /** Total lessons count */
    public int getLessonCount() { return lessons != null ? lessons.size() : 0; }

    /** Count of completed lessons */
    public int getCompletedCount() {
        if (lessons == null) return 0;
        int count = 0;
        for (Lesson l : lessons) if (l.isCompleted()) count++;
        return count;
    }
}
