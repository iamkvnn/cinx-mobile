package com.app.cinx.api.dto;
public class RecommendedCourse {
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private double discountedPrice;
    private double rating;
    private int enrollmentCount;
    private boolean isPublished;
    private boolean isInSubscription;
    private int duration;
    private double score;
    private String source;
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }
    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean isPublished) { this.isPublished = isPublished; }
    public boolean isInSubscription() { return isInSubscription; }
    public void setInSubscription(boolean isInSubscription) { this.isInSubscription = isInSubscription; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
