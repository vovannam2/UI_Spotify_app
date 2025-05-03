package com.example.spotify_app.internals;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.spotify_app.models.LoginRequest;

public class SharePrefManagerAccount {
    private static final String SHARED_PREF_NAME = "account";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PASSWORD = "keypassword";

    private static SharePrefManagerAccount mInstance;
    private static Context ctx;

    private SharePrefManagerAccount(Context context) {
        ctx = context;
    }

    public static synchronized SharePrefManagerAccount getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SharePrefManagerAccount(context);
        }
        return mInstance;
    }

    public void remember(LoginRequest req) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, req.getEmail());
        editor.putString(KEY_PASSWORD, req.getPassword());
        editor.apply();
    }

    public boolean isRemember() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    public LoginRequest getRemenember() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        LoginRequest req = new LoginRequest();
        req.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        req.setPassword(sharedPreferences.getString(KEY_PASSWORD, null));
        return req;
    }

    public void clear() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
