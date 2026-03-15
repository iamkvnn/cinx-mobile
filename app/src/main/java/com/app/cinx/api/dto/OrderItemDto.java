package com.app.cinx.api.dto;

public class OrderItemDto {
    private String id;
    private String courseId;
    private String title;
    private long   price;
    private long   discountedPrice;

    public String getId()             { return id; }
    public String getCourseId()       { return courseId; }
    public String getTitle()          { return title; }
    public long   getPrice()          { return price; }
    public long   getDiscountedPrice() { return discountedPrice; }
}
