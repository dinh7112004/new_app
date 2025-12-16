// services/TokenInterceptor.java
package com.example.md_08_ungdungfivestore.services;

import android.content.Context;

import com.example.md_08_ungdungfivestore.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        SessionManager sm = SessionManager.getInstance(context);
        String token = sm.getToken();
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        return chain.proceed(builder.build());
    }
}