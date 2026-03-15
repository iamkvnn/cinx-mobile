package com.app.cinx.api.dto;

import java.util.List;

/** Order as returned by the single-order endpoint (includes payment). */
public class OrderDetailDto {
    private String             id;
    private List<OrderItemDto> items;
    private long               totalPrice;
    private long               discounted;
    private String             orderDate;
    private PaymentInfoDto     payment;

    public String             getId()         { return id; }
    public List<OrderItemDto> getItems()      { return items; }
    public long               getTotalPrice() { return totalPrice; }
    public long               getDiscounted() { return discounted; }
    public String             getOrderDate()  { return orderDate; }
    public PaymentInfoDto     getPayment()    { return payment; }
}
