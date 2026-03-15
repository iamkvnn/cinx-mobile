package com.app.cinx.api.dto;

public class GetPaymentUrlRequest {
    private String paymentMethod;
    private String orderId;

    public GetPaymentUrlRequest(String paymentMethod, String orderId) {
        this.paymentMethod = paymentMethod;
        this.orderId       = orderId;
    }
}
