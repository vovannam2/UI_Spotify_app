package com.example.spotify_app.helpers;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistHelper {
    public static void getArtistBySongId(Long songId, final ArtistCallback callback) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getArtistsBySongId(songId).enqueue(new Callback<GenericResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Artist>>> call, Response<GenericResponse<List<Artist>>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    Artist artist = response.body().getData().get(0);
                    callback.onSuccess(artist);
                } else {
                    callback.onFailure(new Exception("No artist found for songId: " + songId));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Artist>>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public static void getArtistById(int artistId, final ArtistCallback callback) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getArtistById(artistId).enqueue(new Callback<GenericResponse<Artist>>() {
            @Override
            public void onResponse(Call<GenericResponse<Artist>> call, Response<GenericResponse<Artist>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    Artist artist = response.body().getData();
                    callback.onSuccess(artist);
                } else {
                    callback.onFailure(new Exception("No artist found for artistId: " + artistId));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Artist>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public static void getSongCountByArtistId(int artistId, final SongCountCallback callback) {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getSongCountByArtistId(artistId).enqueue(new Callback<GenericResponse<Integer>>() {
            @Override
            public void onResponse(Call<GenericResponse<Integer>> call, Response<GenericResponse<Integer>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    int songCount = response.body().getData();
                    callback.onSuccess(songCount);
                } else {
                    callback.onFailure(new Exception("No song count found for artistId: " + artistId));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Integer>> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface ArtistCallback {
        void onSuccess(Artist artist);
        void onFailure(Throwable t);
    }

    public interface SongCountCallback {
        void onSuccess(int songCount);
        void onFailure(Throwable t);
    }
}