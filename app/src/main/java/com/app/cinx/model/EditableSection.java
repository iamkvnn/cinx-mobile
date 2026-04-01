package com.app.cinx.model;

import java.util.ArrayList;
import java.util.List;

public class EditableSection {
    public String id; // null if new
    public String title;
    public String description;
    public Long duration = 0L;
    public List<EditableLesson> lessons = new ArrayList<>();

    public EditableSection() {}
}