// services/PaymentService.java
package com.example.md_08_ungdungfivestore.services;

import com.example.md_08_ungdungfivestore.models.PaymentRequest;
import com.example.md_08_ungdungfivestore.models.PaymentResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PaymentService {
    @Headers("Content-Type: application/json")
    @POST("/api/payment/create") // server also supports /api/payments/create
    Call<PaymentResponse> createPayment(@Body PaymentRequest request);
}