package com.example.spotify_app.activities.auth;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotify_app.R;
import com.example.spotify_app.internals.SharedPrefManagerLanguage;
import com.example.spotify_app.internals.SharedPrefManagerTheme;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class GetStartedActivity extends AppCompatActivity {

    private MaterialButton signUpBtn , signInBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ln1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String language = SharedPrefManagerLanguage.getInstance(getApplicationContext()).getLanguage();
        setLocale(language);
        boolean isDarkMode = SharedPrefManagerTheme.getInstance(this).loadNightModeState();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // set layout view
         setContentView(R.layout.activity_get_started);
        signInBtn =(MaterialButton) findViewById(R.id.signInBtn);
        signUpBtn =(MaterialButton) findViewById(R.id.signUpBtn);
        String language = SharedPrefManagerLanguage.getInstance(getApplicationContext()).getLanguage();
        setLocale(language);
        boolean isDarkMode = SharedPrefManagerTheme.getInstance(this).loadNightModeState();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        // set layout view
        setContentView(R.layout.activity_get_started);
        signInBtn =(MaterialButton) findViewById(R.id.signInBtn);
        signUpBtn =(MaterialButton) findViewById(R.id.signUpBtn);


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GetStartedActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}