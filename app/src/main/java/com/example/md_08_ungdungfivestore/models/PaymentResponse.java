package com.example.md_08_ungdungfivestore.models;

public class PaymentResponse {
    private boolean success;
    private String orderId;
    private String vnpTxnRef;
    private String paymentUrl;
    private String message;

    public boolean isSuccess() { return success; }
    public String getPaymentUrl() { return paymentUrl; }
    public String getOrderId() { return orderId; }
    public String getVnpTxnRef() { return vnpTxnRef; }
    public String getMessage() { return message; }


}