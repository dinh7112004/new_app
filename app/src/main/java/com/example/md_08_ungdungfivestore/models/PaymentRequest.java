package com.example.md_08_ungdungfivestore.models;

public class PaymentRequest {
    private String order_id;
    private double total;
    private String user_id;
    private String orderInfo;

    public PaymentRequest(String orderId, double total, String userId, String orderInfo) {
        this.order_id = orderId;
        this.total = total;
        this.user_id = userId;
        this.orderInfo = orderInfo;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }


    // getters/setters if needed
}