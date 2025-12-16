package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface NotificationService {

    @GET("api/notifications/my-notifications")
    Call<List<Notification>> getMyNotifications();

    @PUT("api/notifications/mark-all-read")
    Call<Void> markAllRead();
}
