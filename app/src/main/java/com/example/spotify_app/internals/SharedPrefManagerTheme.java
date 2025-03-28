package com.example.spotify_app.internals;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManagerTheme {
    private static final String PREF_NAME = "theme";
    private static final String UI_MODE = "ui_mode";
    private static SharedPrefManagerTheme instance;
    private Context context;

    public SharedPrefManagerTheme(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManagerTheme getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManagerTheme(context);
        }
        return instance;
    }

    public void setNightModeState(boolean state) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(UI_MODE, state);
        editor.apply();
    }

    public boolean loadNightModeState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(UI_MODE, false);
    }
}
