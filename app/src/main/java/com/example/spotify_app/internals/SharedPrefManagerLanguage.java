package com.example.spotify_app.internals;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManagerLanguage {
    private static final String PREF_NAME = "language";
    private static final String KEY_LANGUAGE = "key_language";
    private static SharedPrefManagerLanguage instance;
    private Context context;
    public SharedPrefManagerLanguage(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManagerLanguage getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManagerLanguage(context);
        }
        return instance;
    }

    public void saveLanguage(String language) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_LANGUAGE, "vi");
    }

    public void clear() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
