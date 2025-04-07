package com.example.spotify_app.services;

import com.example.spotify_app.models.RegisterRequest;
import com.example.spotify_app.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerModel);
}
