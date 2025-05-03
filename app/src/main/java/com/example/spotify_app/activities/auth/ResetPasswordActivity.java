package com.example.spotify_app.activities.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotify_app.R;
import com.example.spotify_app.models.ResetPasswordRequest;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.utils.Validate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout passwordLayout, passwordAgainLayout;
    private TextInputEditText passwordTxt, passwordAgainTxt;
    private MaterialButton btnReset;
    private ProgressBar progressBar;
    private FrameLayout overlay;
    private APIService apiService;
    private Validate validate = new Validate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mapping();

        setEvent(passwordLayout, passwordTxt, "password");
        setEvent(passwordAgainLayout, passwordAgainTxt, "passwordAgain");

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordLayout.getError() == null && passwordAgainLayout.getError() == null) {
                    progressBar.setVisibility(View.VISIBLE);
                    overlay.setBackgroundColor(Color.argb(89, 0, 0, 0));
                    overlay.setVisibility(View.VISIBLE);
                    overlay.setFocusable(true);
                    overlay.setClickable(true);
                    changePassword();
                }
            }
        });
    }

    private void changePassword() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        ResetPasswordRequest req = new ResetPasswordRequest();
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        Log.d("email", email);
        req.setEmail(email);
        req.setPassword(passwordTxt.getText().toString());
        Log.d("password", passwordTxt.getText().toString());

        apiService.changePassword(req).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                progressBar.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.INVISIBLE);
                overlay.setFocusable(false);
                overlay.setClickable(false);
                ResponseMessage res = response.body();
                if(res == null) {
                    Toast.makeText(ResetPasswordActivity.this, "Encounter Error!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ResetPasswordActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                if(res.isSuccess()) {
                    Intent intent1 = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.INVISIBLE);
                overlay.setFocusable(false);
                overlay.setClickable(false);
                Toast.makeText(ResetPasswordActivity.this, "Call API Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEvent(TextInputLayout textInputLayout, TextInputEditText textInput, String type) {
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inp = textInput.getText().toString();
                if(inp.length() == 0) {
                    textInputLayout.setError(getText(R.string.error_required_field));
                } else {
                    if(type.equals("password") && !validate.validatePassword(inp)) {
                        textInputLayout.setError(getText(R.string.error_invalid_password));
                    } else if(type.equals("passwordAgain") && !passwordTxt.getText().toString().equals(inp)) {
                        textInputLayout.setError(getText(R.string.error_password_not_match));
                    } else {
                        textInputLayout.setError(null);
                    }
                }
            }
        });

        textInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String inp = textInput.getText().toString();
                if(b) {
                    if(inp.length() != 0) {
                        if(type.equals("password") && !validate.validatePassword(inp)) {
                            textInputLayout.setError(getText(R.string.error_invalid_password));
                        } else if(type.equals("passwordAgain") && !passwordTxt.getText().toString().equals(inp)) {
                            textInputLayout.setError(getText(R.string.error_password_not_match));
                        }
                        return;
                    }
                    textInputLayout.setError(null);
                } else {
                    if(inp.length() == 0) {
                        textInputLayout.setError(getText(R.string.error_required_field));
                    }
                }
            }
        });
    }

    private void mapping() {
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        passwordAgainLayout = (TextInputLayout) findViewById(R.id.passwordAgainLayout);
        passwordTxt = (TextInputEditText) findViewById(R.id.passwordTxt);
        passwordAgainTxt = (TextInputEditText) findViewById(R.id.passwordAgainTxt);
        btnReset = (MaterialButton) findViewById(R.id.btnReset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        overlay = (FrameLayout) findViewById(R.id.overlay);
    }
}