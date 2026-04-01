package com.app.cinx.model;

public class EditableLesson {
    public String id; // null if new
    public String title;
    public Long duration = 0L;
    public String lessonType = "VIDEO";

    public EditableLesson() {}
}