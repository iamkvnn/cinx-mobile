package com.app.cinx.api.dto;

import java.util.List;

public class CreateOrderRequest {
    private List<CartItemDto> cartItems;
    public List<CartItemDto> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItemDto> val) { this.cartItems = val; }

    private String paymentMethod;
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String val) { this.paymentMethod = val; }

    private String voucherCode;
    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String val) { this.voucherCode = val; }

}