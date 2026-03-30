package com.app.cinx.api.dto;

import java.util.List;

public class PaymentResponse {
    private String id;
    public String getId() { return id; }
    public void setId(String val) { this.id = val; }

    private String orderId;
    public String getOrderId() { return orderId; }
    public void setOrderId(String val) { this.orderId = val; }

    private Long amount;
    public Long getAmount() { return amount; }
    public void setAmount(Long val) { this.amount = val; }

    private String status;
    public String getStatus() { return status; }
    public void setStatus(String val) { this.status = val; }

    private String paymentDate;
    public String getPaymentDate() { return paymentDate; }
    public void setPaymentDate(String val) { this.paymentDate = val; }

    private String paymentInfo;
    public String getPaymentInfo() { return paymentInfo; }
    public void setPaymentInfo(String val) { this.paymentInfo = val; }

    private String paymentMessage;
    public String getPaymentMessage() { return paymentMessage; }
    public void setPaymentMessage(String val) { this.paymentMessage = val; }

}