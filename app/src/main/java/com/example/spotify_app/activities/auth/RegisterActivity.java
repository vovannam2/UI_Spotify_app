package com.example.spotify_app.activities.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.spotify_app.R;

import com.example.spotify_app.models.RegisterRequest;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.models.RegisterResponse;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText inputName, inputPassword, inputemail;
    Button buttonRegister;
    TextView linklogin;

    private APIService apiService;
    private ProgressBar progressBar;
    private FrameLayout overlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputName = findViewById(R.id.txtName);
        inputemail = findViewById(R.id.txtEmail);
        inputPassword = findViewById(R.id.txtPwd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        linklogin = findViewById(R.id.lnkLogin);
        linklogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        buttonRegister = findViewById(R.id.btnregister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (inputName.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Tên không được trống!", Toast.LENGTH_SHORT).show();
                } else if (inputemail.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Email không được trống!", Toast.LENGTH_SHORT).show();
                } else if (inputPassword.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không được trống!", Toast.LENGTH_SHORT).show();
                } else if (emailValidator(inputemail.getText().toString()) == false) {
                    Toast.makeText(RegisterActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                } else if (inputPassword.getText().toString().length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải ít nhất 6 kí tự!", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    overlay.setBackgroundColor(Color.argb(89, 0, 0, 0));
                    overlay.setVisibility(View.VISIBLE);
                    overlay.setFocusable(true);
                    overlay.setClickable(true);

                    RegisterRequest registerModel = new RegisterRequest();
                    registerModel.setFirstName(inputName.getText().toString());
                    registerModel.setEmail(inputemail.getText().toString());
                    registerModel.setPassword(inputPassword.getText().toString());
                    register(registerModel);
                }
            }
        });


    }
    public void register (RegisterRequest registerModel){
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.register(registerModel).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progressBar.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.INVISIBLE);
                overlay.setFocusable(false);
                overlay.setClickable(false);
                if (response.isSuccessful() && response.body() != null) {
                    // Xử lý khi API trả về thành công
                    Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("type", "confirm");
                    intent.putExtra("email", inputemail.getText());
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý khi API trả về lỗi (ví dụ: mã lỗi 400, 500)
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.INVISIBLE);
                overlay.setFocusable(false);
                overlay.setClickable(false);
                if (t instanceof java.net.UnknownHostException) {
                    Toast.makeText(RegisterActivity.this, "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

}