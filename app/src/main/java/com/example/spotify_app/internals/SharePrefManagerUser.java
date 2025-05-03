package com.example.spotify_app.internals;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.spotify_app.models.User;

public class SharePrefManagerUser {
    private static final String SHARED_PREF_NAME = "user";
    private static final String KEY_ID = "keyid";
    private static final String KEY_FIRST_NAME = "keyfirstname";
    private static final String KEY_LAST_NAME = "keylastname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_GENDER = "keygender";
    private static final String KEY_IMAGE = "keyimage";
    private static final String KEY_PHONENUMBER = "keyphonenumber";
    private static final String KEY_ACCESS_TOKEN = "keyaccess";
    private static final String KEY_REFRESH_TOKEN = "keyrefresh";

    private static final String KEY_PROVIDER = "keyprovider";

    private static SharePrefManagerUser mInstance;
    private static Context ctx;

    private SharePrefManagerUser(Context context) {
        ctx = context;
    }

    public static synchronized SharePrefManagerUser getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new SharePrefManagerUser(context);
        }
        return mInstance;
    }

    public void loginSuccess(User user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FIRST_NAME, user.getFirstName());
        editor.putString(KEY_LAST_NAME, user.getLastName());
        editor.putInt(KEY_GENDER, user.getGender());
        editor.putString(KEY_IMAGE, user.getAvatar());
        editor.putString(KEY_PHONENUMBER, user.getPhoneNumber());
        editor.putString(KEY_ACCESS_TOKEN, user.getAccessToken());
        editor.putString(KEY_REFRESH_TOKEN, user.getRefreshToken());
        editor.putString(KEY_PROVIDER,user.getProvider());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null) != null;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        User user = new User();
        user.setId(sharedPreferences.getInt(KEY_ID, -1));
        user.setAvatar(sharedPreferences.getString(KEY_IMAGE, null));
        user.setGender(sharedPreferences.getInt(KEY_GENDER, -1));
        user.setEmail(sharedPreferences.getString(KEY_EMAIL, null));
        user.setPhoneNumber(sharedPreferences.getString(KEY_PHONENUMBER, null));
        user.setFirstName(sharedPreferences.getString(KEY_FIRST_NAME, null));
        user.setLastName(sharedPreferences.getString(KEY_LAST_NAME, null));
        user.setAccessToken(sharedPreferences.getString(KEY_ACCESS_TOKEN, null));
        user.setRefreshToken(sharedPreferences.getString(KEY_REFRESH_TOKEN, null));
        user.setProvider(sharedPreferences.getString(KEY_PROVIDER,null));
        return user;
    }

    public void logout() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        /*ctx.startActivity(new Intent(ctx, LoginActivity.class));*/
    }

}
