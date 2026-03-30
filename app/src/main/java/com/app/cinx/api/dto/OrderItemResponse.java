package com.app.cinx.api.dto;

import java.util.List;

public class OrderItemResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String courseId;
    public String getCourseId() { return courseId; }
    public void setCourseId(String val) { this.courseId = val; }

    private String title;
    public String getTitle() { return title; }
    public void setTitle(String val) { this.title = val; }

    private Long price;
    public Long getPrice() { return price; }
    public void setPrice(Long val) { this.price = val; }

    private Long discountedPrice;
    public Long getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(Long val) { this.discountedPrice = val; }

}