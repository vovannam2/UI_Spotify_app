package com.example.spotify_app.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerModel);
}
