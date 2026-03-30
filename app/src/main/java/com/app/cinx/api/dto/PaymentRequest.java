package com.app.cinx.api.dto;

import java.util.List;

public class PaymentRequest {
    private String orderId;
    public String getOrderId() { return orderId; }
    public void setOrderId(String val) { this.orderId = val; }

    private String paymentMethod;
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String val) { this.paymentMethod = val; }

}