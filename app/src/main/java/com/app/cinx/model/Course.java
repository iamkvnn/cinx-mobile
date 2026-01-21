package com.app.cinx.model;

public class Course {
    private int id;
    private String title;
    private String instructor;
    private double rating;
    private String students;
    private String price;
    private String imageUrl;
    private String category;
    private String duration; // e.g. "22h"

    public Course(int id, String title, String instructor, double rating, String students, String price, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.rating = rating;
        this.students = students;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.duration = "2h"; // Default
    }

    // Constructor with duration
    public Course(int id, String title, String instructor, double rating, String students, String price, String imageUrl, String category, String duration) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.rating = rating;
        this.students = students;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getInstructor() {
        return instructor;
    }

    public double getRating() {
        return rating;
    }

    public String getStudents() {
        return students;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }
    
    public String getDuration() {
        return duration;
    }
}
