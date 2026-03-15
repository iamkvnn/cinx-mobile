package com.app.cinx.api.dto;

public class CourseDto {
    private String  id;
    private String  title;
    private String  description;
    private String  category;
    private long    price;
    private long    discountedPrice;
    private long    discountRate;
    private double  rating;
    private int     enrollmentCount;
    private boolean isPublished;
    private boolean isInSubscription;
    private int     duration;
    private String  createdAt;
    private String  updatedAt;

    public String  getId()               { return id; }
    public String  getTitle()            { return title; }
    public String  getDescription()      { return description; }
    public String  getCategory()         { return category; }
    public long    getPrice()            { return price; }
    public long    getDiscountedPrice()  { return discountedPrice; }
    public long    getDiscountRate()     { return discountRate; }
    public double  getRating()           { return rating; }
    public int     getEnrollmentCount()  { return enrollmentCount; }
    public boolean isPublished()         { return isPublished; }
    public boolean isInSubscription()    { return isInSubscription; }
    public int     getDuration()         { return duration; }
    public String  getCreatedAt()        { return createdAt; }
    public String  getUpdatedAt()        { return updatedAt; }
}
