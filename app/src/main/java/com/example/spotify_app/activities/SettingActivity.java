package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.auth.LoginActivity;
import com.example.spotify_app.activities.auth.ResetPasswordActivity;
import com.example.spotify_app.helpers.DialogHelper;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.internals.SharedPrefManagerLanguage;
import com.example.spotify_app.internals.SharedPrefManagerTheme;
import com.example.spotify_app.listeners.DialogClickListener;
import com.example.spotify_app.models.User;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingActivity extends AppCompatActivity {
    TextView changePassword, logout;
    RadioButton english, vietnamese;

    SwitchMaterial darkModeSwitch;
    User user;
    private CompoundButton.OnCheckedChangeListener listener;

    ImageView imageView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = SharePrefManagerUser.getInstance(this).getUser();
        String language = SharedPrefManagerLanguage.getInstance(getApplicationContext()).getLanguage();
        boolean isDarkMode = SharedPrefManagerTheme.getInstance(this).loadNightModeState();
        setContentView(R.layout.activity_setting);
        mapping();

        if (language.equals("vi")) {
            vietnamese.setChecked(true);
            english.setChecked(false);
        } else {
            vietnamese.setChecked(false);
            english.setChecked(true);
        }

        darkModeSwitch.setChecked(isDarkMode);

        int nightMode = AppCompatDelegate.getDefaultNightMode();
        darkModeSwitch.setChecked(nightMode == AppCompatDelegate.MODE_NIGHT_YES);
        listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                DialogHelper dialog = new DialogHelper(SettingActivity.this, new DialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                       restartApplication();
                       SharedPrefManagerTheme.getInstance(getApplicationContext()).setNightModeState(isChecked);
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        // Temporarily remove the listener
                        darkModeSwitch.setOnCheckedChangeListener(null);

                        // Set the switch to unchecked
                        darkModeSwitch.setChecked(!isChecked);
                        // Add the listener back
                        darkModeSwitch.post(new Runnable() {
                            @Override
                            public void run() {
                                darkModeSwitch.setOnCheckedChangeListener(listener);
                            }
                        });
                    }
                });
                dialog.show();
                dialog.setTitle(getString(R.string.dialog_title_confirm_restart));
                dialog.setMessage(getString(R.string.dialog_message_theme_confirm_restart));
                dialog.setPositiveButtonContent(getString(R.string.button_ok));
                dialog.setNegativeButtonContent(getString(R.string.button_cancel));
            }
        };
        darkModeSwitch.setOnCheckedChangeListener(listener);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                removeToken(user.getId());
                SharePrefManagerUser.getInstance(getApplicationContext()).logout();
                finish();
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper dialog = new DialogHelper(view.getContext(), new DialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        restartApplication();
                        SharedPrefManagerLanguage.getInstance(getApplicationContext()).saveLanguage("en");
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        english.setChecked(false);
                        vietnamese.setChecked(true);
                    }
                });
                dialog.show();
                dialog.setTitle(getString(R.string.dialog_title_confirm_restart));
                dialog.setMessage(getString(R.string.dialog_message_language_confirm_restart));
                dialog.setPositiveButtonContent(getString(R.string.button_ok));
                dialog.setNegativeButtonContent(getString(R.string.button_cancel));
            }
        });

        vietnamese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper dialog = new DialogHelper(view.getContext(), new DialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        restartApplication();
                        SharedPrefManagerLanguage.getInstance(getApplicationContext()).saveLanguage("vi");
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        vietnamese.setChecked(false);
                        english.setChecked(true);
                    }
                });
                dialog.show();
                dialog.setTitle(getString(R.string.dialog_title_confirm_restart));
                dialog.setMessage(getString(R.string.dialog_message_language_confirm_restart));
                dialog.setPositiveButtonContent(getString(R.string.button_ok));
                dialog.setNegativeButtonContent(getString(R.string.button_cancel));
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backActivity();
            }
        });
    }

    private void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void mapping() {
        changePassword = (TextView) findViewById(R.id.ChangePasswordTxt);
        logout = (TextView) findViewById(R.id.LogoutTxt);
        english = (RadioButton) findViewById(R.id.rad_english);
        vietnamese = (RadioButton) findViewById(R.id.rad_vietnamese);
        imageView = (ImageView) findViewById(R.id.back_icon);
        darkModeSwitch = (SwitchMaterial) findViewById(R.id.switch_dark_mode);
    }

    void removeToken(int id) {
        String userId = String.valueOf(id);
        Log.e("removeToken", userId);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://music-app-967da-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference tokenRef = database.getReference("tokenPhone");
        tokenRef.child((userId)).removeValue();
    }
    private void backActivity(){
        finish();
    }
}
