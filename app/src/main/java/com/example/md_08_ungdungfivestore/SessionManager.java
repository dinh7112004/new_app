// utils/SessionManager.java
package com.example.md_08_ungdungfivestore;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "app_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";

    private static SessionManager instance;
    private final SharedPreferences prefs;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) instance = new SessionManager(context);
        return instance;
    }

    public void saveUser(String userId, String token) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public String getUserId() { return prefs.getString(KEY_USER_ID, ""); }
    public String getToken() { return prefs.getString(KEY_TOKEN, ""); }
    public void clear() { prefs.edit().clear().apply(); }
}