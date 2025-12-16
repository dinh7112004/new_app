package com.example.md_08_ungdungfivestore.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Notification {

    @SerializedName("_id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    private String type;
    private String title;
    private String message;

    @SerializedName("order_id")
    private String orderId;

    private String image;
    private String productName;
    private boolean read;

    private Date createdAt;

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getImage() {
        return image;
    }

    public String getProductName() {
        return productName;
    }

    public boolean isRead() {
        return read;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
