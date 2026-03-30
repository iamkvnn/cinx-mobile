package com.app.cinx.api.dto;

import java.util.List;

public class OrderResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String userId;
    public String getUserId() { return userId; }
    public void setUserId(String val) { this.userId = val; }

    private List<OrderItemResponse> items;
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> val) { this.items = val; }

    private Long totalPrice;
    public Long getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Long val) { this.totalPrice = val; }

    private Long discounted;
    public Long getDiscounted() { return discounted; }
    public void setDiscounted(Long val) { this.discounted = val; }

    private String orderDate;
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String val) { this.orderDate = val; }

}