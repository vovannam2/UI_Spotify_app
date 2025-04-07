package com.example.spotify_app.activities.auth;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotify_app.R;
import com.example.spotify_app.internals.SharedPrefManagerLanguage;
import com.example.spotify_app.internals.SharedPrefManagerTheme;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class GetStartedActivity extends AppCompatActivity {

    private AppCompatButton signUpBtn, signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Set ngôn ngữ
        String language = SharedPrefManagerLanguage.getInstance(getApplicationContext()).getLanguage();
        setLocale(language);

        // Set chế độ tối
        boolean isDarkMode = SharedPrefManagerTheme.getInstance(this).loadNightModeState();
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Giao diện
        setContentView(R.layout.activity_get_started);

        // Căn chỉnh lề khi có status bar, navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ln1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Ánh xạ nút
        signInBtn = findViewById(R.id.signInBtn);
        signUpBtn = findViewById(R.id.signUpBtn);

        // Xử lý sự kiện
        signInBtn.setOnClickListener(view -> {
            Intent intent = new Intent(GetStartedActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signUpBtn.setOnClickListener(view -> {
            Intent intent = new Intent(GetStartedActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }
}
