package com.app.cinx.model;

public class Course {
    private int id;
    private String title;
    private String instructor;
    private double rating;
    private String students;
    private long price;
    private long discountedPrice;
    private long discountRate;
    private String imageUrl;
    private String category;
    private String duration; // e.g. "22h"

    public Course(int id, String title, String instructor, double rating, String students, long price, String imageUrl, String category) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.rating = rating;
        this.students = students;
        this.price = price;
        this.discountedPrice = price; this.discountRate = 0;
        this.imageUrl = imageUrl;
        this.category = category;
        this.duration = "2h"; // Default
    }

    // Constructor with duration
    public Course(int id, String title, String instructor, double rating, String students, long price, String imageUrl, String category, String duration) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.rating = rating;
        this.students = students;
        this.price = price;
        this.discountedPrice = price; this.discountRate = 0;
        this.imageUrl = imageUrl;
        this.category = category;
        this.duration = duration;
    }

    // Constructor with discounts
    public Course(int id, String title, String instructor, double rating, String students, long price, long discountedPrice, long discountRate, String imageUrl, String category, String duration) {
        this.id = id;
        this.title = title;
        this.instructor = instructor;
        this.rating = rating;
        this.students = students;
        this.price = price;
        this.discountedPrice = discountedPrice; this.discountRate = discountRate;
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

    public long getPrice() {
        return price;
    }

    public long getDiscountedPrice() {
        return discountedPrice;
    }

    public long getDiscountRate() {
        return discountRate;
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
