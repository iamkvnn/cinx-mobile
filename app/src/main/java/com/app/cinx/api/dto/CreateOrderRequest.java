package com.app.cinx.api.dto;

import java.util.List;

public class CreateOrderRequest {
    private String          paymentMethod;
    private List<CartItemDto> cartItems;

    public CreateOrderRequest(String paymentMethod, List<CartItemDto> cartItems) {
        this.paymentMethod = paymentMethod;
        this.cartItems     = cartItems;
    }
}
