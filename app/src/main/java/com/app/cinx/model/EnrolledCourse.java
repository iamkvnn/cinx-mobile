package com.app.cinx.model;

/**
 * EnrolledCourse — represents a course that belongs to the current user in one
 * of three states: in-progress, completed, or saved/bookmarked.
 *
 * Uses static factory methods instead of overloaded constructors so call-sites
 * are self-documenting and each status variant only carries the fields it needs.
 */
public class EnrolledCourse {

    // ── Status ───────────────────────────────────────────────────────────────

    public enum Status {
        PROGRESS, COMPLETED, SAVED
    }

    // ── Shared fields ────────────────────────────────────────────────────────

    private final int    id;
    private final String title;
    private final String instructor;
    private final String imageUrl;
    private final String category;
    private final Status status;

    // ── PROGRESS-specific ────────────────────────────────────────────────────

    private int    progress;      // 0–100
    private String nextLesson;
    private String lastAccessed;

    // ── COMPLETED-specific ───────────────────────────────────────────────────

    private String completionDate;
    private String grade;

    // ── SAVED-specific ───────────────────────────────────────────────────────

    private String originalPrice;
    private double rating;

    // ── Private base constructor ─────────────────────────────────────────────

    private EnrolledCourse(int id, String title, String instructor,
                           String imageUrl, String category, Status status) {
        this.id         = id;
        this.title      = title;
        this.instructor = instructor;
        this.imageUrl   = imageUrl;
        this.category   = category;
        this.status     = status;
    }

    // ── Static factory methods ───────────────────────────────────────────────

    public static EnrolledCourse progress(int id, String title, String instructor,
                                          String imageUrl, String category,
                                          int progress, String nextLesson,
                                          String lastAccessed) {
        EnrolledCourse c = new EnrolledCourse(id, title, instructor,
                imageUrl, category, Status.PROGRESS);
        c.progress     = progress;
        c.nextLesson   = nextLesson;
        c.lastAccessed = lastAccessed;
        return c;
    }

    public static EnrolledCourse completed(int id, String title, String instructor,
                                           String imageUrl, String category,
                                           String completionDate, String grade) {
        EnrolledCourse c = new EnrolledCourse(id, title, instructor,
                imageUrl, category, Status.COMPLETED);
        c.completionDate = completionDate;
        c.grade          = grade;
        return c;
    }

    public static EnrolledCourse saved(int id, String title, String instructor,
                                       String imageUrl, String category,
                                       String originalPrice, double rating) {
        EnrolledCourse c = new EnrolledCourse(id, title, instructor,
                imageUrl, category, Status.SAVED);
        c.originalPrice = originalPrice;
        c.rating        = rating;
        return c;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public int    getId()             { return id; }
    public String getTitle()          { return title; }
    public String getInstructor()     { return instructor; }
    public String getImageUrl()       { return imageUrl; }
    public String getCategory()       { return category; }
    public Status getStatus()         { return status; }
    public int    getProgress()       { return progress; }
    public String getNextLesson()     { return nextLesson; }
    public String getLastAccessed()   { return lastAccessed; }
    public String getCompletionDate() { return completionDate; }
    public String getGrade()          { return grade; }
    public String getOriginalPrice()  { return originalPrice; }
    public double getRating()         { return rating; }
}
