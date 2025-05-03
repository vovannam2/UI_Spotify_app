package com.example.spotify_app.activities.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.BaseActivity;
import com.example.spotify_app.internals.SharePrefManagerAccount;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.ForgotPassword;
import com.example.spotify_app.models.LoginRequest;
import com.example.spotify_app.models.LoginResponse;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.utils.Const;
import com.example.spotify_app.utils.Validate;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword;
    Button buttonLogin;
    private CheckBox checkBoxRemember;
    private FrameLayout overlay;
    private ProgressBar progressBar;
    private TextView textCreateAccount, forgotPasswordText;
    private MaterialButton btnGetOtp ;

    private Validate validate = new Validate();
    private APIService apiService;
    private String email = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mapping();

        textCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginRequest req = new LoginRequest();
                req.setEmail(inputEmail.getText().toString());
                req.setPassword(inputPassword.getText().toString());
                req.setRole("USER");
                if(checkSuccess()) {
                    openOverlay();
                    if(checkBoxRemember.isChecked()) {
                        SharePrefManagerAccount.getInstance(getApplicationContext()).remember(req);
                    } else {
                        SharePrefManagerAccount.getInstance(getApplicationContext()).clear();
                    }
                    login(req);
                }
            }
        });

    }
    private void login(LoginRequest req) {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.authenticate(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                hideOverlay();
                LoginResponse res = response.body();
                if(res == null) {
                    Toast.makeText(LoginActivity.this, getText(R.string.error_wrong_account_infor), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(res.isSuccess()) {
                    User user = new User();
                    user.setFirstName(res.getFirstName());
                    user.setLastName(res.getLastName());
                    user.setAvatar(res.getAvatar());
                    user.setEmail(res.getEmail());
                    user.setGender(res.getGender());
                    user.setId(res.getId());
                    user.setAccessToken(res.getAccessToken());
                    Const.setAccessToken(res.getAccessToken());
                    user.setRefreshToken(res.getRefreshToken());
                    user.setProvider(res.getProvider());

                    SharePrefManagerUser.getInstance(getApplicationContext()).loginSuccess(user);

                    Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                    startActivity(intent);
                    finish();
                }
                Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                if(res.getType() != null && res.getType().equals("confirm")) {
                    email = res.getEmail();
                    sendOtp();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                hideOverlay();
                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sendOtp() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.setEmail(email);
        apiService.sendOtp(forgotPassword).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                hideOverlay();
                ResponseMessage res = new ResponseMessage();
                res = response.body();
                if(res == null) {
                    Toast.makeText(LoginActivity.this, "Encounter Error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println(res.getMessage());
                Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                if(res.isSuccess()) {
                    Intent intent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
                    intent.putExtra("type", "confirm");
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                hideOverlay();
                Toast.makeText(LoginActivity.this, "Call API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void mapping(){
        textCreateAccount = findViewById(R.id.lnkRegister);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        inputEmail = findViewById(R.id.txtloginEmail);
        inputPassword = findViewById(R.id.txtloginPwd);
        buttonLogin = findViewById(R.id.btnlogin);
        checkBoxRemember = (CheckBox) findViewById(R.id.checkBoxRemember);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        overlay = (FrameLayout) findViewById(R.id.overlay);
        btnGetOtp=findViewById(R.id.btnGetOtp);
    }
    private void fillText() {
        boolean isRemember = SharePrefManagerAccount.getInstance(getApplicationContext()).isRemember();
        if(isRemember) {
            LoginRequest req = SharePrefManagerAccount.getInstance(getApplicationContext()).getRemenember();
            inputEmail.setText(req.getEmail());
            inputPassword.setText(req.getPassword());
            checkBoxRemember.setChecked(true);
        }
    }
    private boolean checkSuccess() {
        return inputEmail.getError() == null && inputPassword.getError() == null;
    }
    private void openOverlay() {
        overlay.setBackgroundColor(Color.argb(89, 0, 0, 0));
        overlay.setVisibility(View.VISIBLE);
        overlay.setFocusable(true);
        overlay.setClickable(true);
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideOverlay() {
        overlay.setVisibility(View.INVISIBLE);
        overlay.setFocusable(false);
        overlay.setClickable(false);
        progressBar.setVisibility(View.INVISIBLE);
    }
}