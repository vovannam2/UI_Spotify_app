package com.example.spotify_app.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotify_app.R;
import com.example.spotify_app.models.OtpResponse;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpVerifyActivity extends AppCompatActivity {

    private MaterialButton btnVerify;
    private EditText otp1Txt, otp2Txt, otp3Txt, otp4Txt, otp5Txt, otp6Txt;
    private ProgressBar progressBar;
    private TextView countdownTxt;
    private APIService apiService;
    private int countdownDurtion = 15 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        mapping();
        setupOtpInputs();

        CountDownTimer countDownTimer = new CountDownTimer(countdownDurtion, 1000) {
            @Override
            public void onTick(long l) {
                long totalSecond = l / 1000;
                long minutes = totalSecond / 60;
                long seconds = totalSecond % 60;
                String formattedTime = String.format("%02d:%02d", minutes, seconds);
                countdownTxt.setText(formattedTime);
            }

            @Override
            public void onFinish() {
                Toast.makeText(OtpVerifyActivity.this, "Time out!", Toast.LENGTH_SHORT).show();
            }
        };

        countDownTimer.start();

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                verify();
            }
        });
    }

    private void mapping() {
        btnVerify = (MaterialButton) findViewById(R.id.btnVerify);
        otp1Txt = (EditText) findViewById(R.id.opt1Txt);
        otp2Txt = (EditText) findViewById(R.id.opt2Txt);
        otp3Txt = (EditText) findViewById(R.id.opt3Txt);
        otp4Txt = (EditText) findViewById(R.id.opt4Txt);
        otp5Txt = (EditText) findViewById(R.id.opt5Txt);
        otp6Txt = (EditText) findViewById(R.id.opt6Txt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        countdownTxt = (TextView) findViewById(R.id.countdownTxt);
    }

    private void setupOtpInputs() {
        otp1Txt.addTextChangedListener(new GenericTextWatcher(otp1Txt, otp2Txt));
        otp2Txt.addTextChangedListener(new GenericTextWatcher(otp2Txt, otp3Txt));
        otp3Txt.addTextChangedListener(new GenericTextWatcher(otp3Txt, otp4Txt));
        otp4Txt.addTextChangedListener(new GenericTextWatcher(otp4Txt, otp5Txt));
        otp5Txt.addTextChangedListener(new GenericTextWatcher(otp5Txt, otp6Txt));
        otp6Txt.addTextChangedListener(new GenericTextWatcher(otp6Txt, null)); // Không có ô tiếp theo
    }

    private class GenericTextWatcher implements TextWatcher {
        private final EditText currentView;
        private final EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().isEmpty() && nextView != null) {
                nextView.requestFocus(); // Chuyển con trỏ sang ô tiếp theo
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }

    private void verify() {
        String token = otp1Txt.getText().toString() + otp2Txt.getText().toString() + otp3Txt.getText().toString() + otp4Txt.getText().toString() + otp5Txt.getText().toString() + otp6Txt.getText().toString();
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        String email = intent.getStringExtra("email");

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.verifyOtp(token, type).enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(OtpVerifyActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                OtpResponse res = response.body();
                if(res == null) {
                    return;
                }
                if(res.isSuccess()) {
                    Intent intent1 = new Intent();
                    if(res.getType().equals("confirm")) {
                        intent1 = new Intent(OtpVerifyActivity.this, LoginActivity.class);
                    } else if(res.getType().equals("forgot")) {
                        intent1 = new Intent(OtpVerifyActivity.this, ResetPasswordActivity.class);
                        intent1.putExtra("email", email);
                    }
                    startActivity(intent1);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                Toast.makeText(OtpVerifyActivity.this, "Call API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


}