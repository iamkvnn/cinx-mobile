package com.app.cinx.model;

import java.util.List;

/**
 * Represents a single learning lesson.
 * Supports VIDEO, DOCUMENT, and QUIZ types.
 * Designed to be easily extended with new fields or types.
 */
public class Lesson {
    // Identification
    private final int id;
    private final String title;
    private final LessonType type;

    // Meta
    private final String duration;       // e.g. "12:30", "5 phút", "10 câu"
    private final int chapterNumber;
    private final String chapterTitle;
    private final int lessonNumber;       // lesson position within the chapter

    // State
    private boolean completed;
    private boolean locked;
    private boolean active;              // currently being viewed
    private boolean preview;             // free preview, show "Xem trước" badge

    // VIDEO-specific
    private final String videoThumbnailUrl;

    // DOCUMENT-specific
    private final String documentHtml;   // HTML content for rich text rendering

    // QUIZ-specific
    private final List<QuizQuestion> questions;

    private Lesson(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.type = builder.type;
        this.duration = builder.duration;
        this.chapterNumber = builder.chapterNumber;
        this.chapterTitle = builder.chapterTitle;
        this.lessonNumber = builder.lessonNumber;
        this.completed = builder.completed;
        this.locked = builder.locked;
        this.active = builder.active;
        this.preview = builder.preview;
        this.videoThumbnailUrl = builder.videoThumbnailUrl;
        this.documentHtml = builder.documentHtml;
        this.questions = builder.questions;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public LessonType getType() { return type; }
    public String getDuration() { return duration; }
    public int getChapterNumber() { return chapterNumber; }
    public String getChapterTitle() { return chapterTitle; }
    public int getLessonNumber() { return lessonNumber; }
    public boolean isCompleted() { return completed; }
    public boolean isLocked() { return locked; }
    public boolean isActive() { return active; }
    public boolean isPreview() { return preview; }
    public String getVideoThumbnailUrl() { return videoThumbnailUrl; }
    public String getDocumentHtml() { return documentHtml; }
    public List<QuizQuestion> getQuestions() { return questions; }

    // Mutable state setters
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setLocked(boolean locked) { this.locked = locked; }
    public void setActive(boolean active) { this.active = active; }
    public void setPreview(boolean preview) { this.preview = preview; }

    /**
     * Returns a human-readable type label for display in the bottom sheet.
     */
    public String getTypeLabel() {
        switch (type) {
            case VIDEO:    return "Video";
            case DOCUMENT: return "Tài liệu";
            case QUIZ:     return "Trắc nghiệm";
            default:       return "";
        }
    }

    // ------------------------------------------------------------------ //
    //  Builder — allows clean construction without telescoping constructors
    // ------------------------------------------------------------------ //
    public static class Builder {
        private final int id;
        private final String title;
        private final LessonType type;

        private String duration = "";
        private int chapterNumber = 1;
        private String chapterTitle = "";
        private int lessonNumber = 1;
        private boolean completed = false;
        private boolean locked = false;
        private boolean active = false;
        private boolean preview = false;
        private String videoThumbnailUrl = "";
        private String documentHtml = "";
        private List<QuizQuestion> questions = null;

        public Builder(int id, String title, LessonType type) {
            this.id = id;
            this.title = title;
            this.type = type;
        }

        public Builder duration(String d)              { this.duration = d;            return this; }
        public Builder chapterNumber(int n)            { this.chapterNumber = n;       return this; }
        public Builder chapterTitle(String t)          { this.chapterTitle = t;        return this; }
        public Builder lessonNumber(int n)             { this.lessonNumber = n;        return this; }
        public Builder completed(boolean v)            { this.completed = v;           return this; }
        public Builder locked(boolean v)               { this.locked = v;              return this; }
        public Builder active(boolean v)               { this.active = v;              return this; }
        public Builder preview(boolean v)              { this.preview = v;             return this; }
        public Builder videoThumbnailUrl(String url)   { this.videoThumbnailUrl = url; return this; }
        public Builder documentHtml(String html)       { this.documentHtml = html;     return this; }
        public Builder questions(List<QuizQuestion> q) { this.questions = q;           return this; }

        public Lesson build() { return new Lesson(this); }
    }
}
