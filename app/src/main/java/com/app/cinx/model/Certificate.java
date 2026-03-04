package com.app.cinx.model;

/**
 * Represents a course completion certificate earned by the user.
 */
public class Certificate {

    public enum Grade { EXCELLENT, GOOD, AVERAGE }

    // ─────────────────────────────────────────────────────────────────
    // Fields
    // ─────────────────────────────────────────────────────────────────

    private final String id;
    private final String courseName;
    private final String issuedDate;   // display string, e.g. "20/05/2026"
    private final int    score;        // 0–100
    private final Grade  grade;
    private final String pdfUrl;       // link to download / share

    // ─────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────

    public Certificate(String id, String courseName, String issuedDate,
                       int score, Grade grade, String pdfUrl) {
        this.id         = id;
        this.courseName = courseName;
        this.issuedDate = issuedDate;
        this.score      = score;
        this.grade      = grade;
        this.pdfUrl     = pdfUrl;
    }

    // ─────────────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────────────

    public String getId()         { return id; }
    public String getCourseName() { return courseName; }
    public String getIssuedDate() { return issuedDate; }
    public int    getScore()      { return score; }
    public Grade  getGrade()      { return grade; }
    public String getPdfUrl()     { return pdfUrl; }

    /** Returns the display label for the grade badge. */
    public String getGradeLabel() {
        switch (grade) {
            case EXCELLENT: return "Xuất sắc";
            case GOOD:      return "Khá";
            default:        return "Trung bình";
        }
    }

    /** Returns score formatted as "(score/100)". */
    public String getScoreLabel() {
        return "(" + score + "/100)";
    }
}
