package com.app.cinx.api.dto;

public class PaymentInfoDto {
    private String id;
    private long   amount;
    private String status;
    private String paymentDate;
    private String paymentInfo;
    private String paymentMessage;

    public String getId()             { return id; }
    public long   getAmount()         { return amount; }
    public String getStatus()         { return status; }
    public String getPaymentDate()    { return paymentDate; }
    public String getPaymentInfo()    { return paymentInfo; }
    public String getPaymentMessage() { return paymentMessage; }
}
