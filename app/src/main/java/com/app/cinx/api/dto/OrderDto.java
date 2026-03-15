package com.app.cinx.api.dto;

import java.util.List;

/** Order as returned in the list endpoint (no payment details). */
public class OrderDto {
    private String           id;
    private List<OrderItemDto> items;
    private long             totalPrice;
    private long             discounted;
    private String           orderDate;

    public String             getId()         { return id; }
    public List<OrderItemDto> getItems()      { return items; }
    public long               getTotalPrice() { return totalPrice; }
    public long               getDiscounted() { return discounted; }
    public String             getOrderDate()  { return orderDate; }
}
