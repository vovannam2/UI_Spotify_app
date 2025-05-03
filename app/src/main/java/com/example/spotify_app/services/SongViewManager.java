package com.example.spotify_app.services;

import android.os.Handler;
import android.util.Log;

import androidx.media3.common.MediaItem;

import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SongViewManager {
    private static SongViewManager instance;
    private Handler handler;
    private Runnable runnable;
    private APIService apiService;
    private MediaItem mediaItem;

    private SongViewManager() {
        handler = new Handler();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaItem != null) {
                    Log.d("SongViewManager", "run: increase view of song by id: " + mediaItem.mediaMetadata.extras.getLong("id"));
                    apiService.increaseViewOfSongBySongId(mediaItem.mediaMetadata.extras.getLong("id")).enqueue(new Callback<GenericResponse<Song>>() {
                        @Override
                        public void onResponse(Call<GenericResponse<Song>> call, Response<GenericResponse<Song>> response) {
                            if (response.body() != null) {
                                Log.d("SongViewManager", "onResponse: " + response.body().getData().getViews());
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse<Song>> call, Throwable t) {

                        }
                    });
                }
            }
        };
    }

    public static SongViewManager getInstance() {
        if (instance == null) {
            return new SongViewManager();
        }
        return instance;
    }

    public void startCount(MediaItem mediaItem) {
        this.mediaItem = mediaItem;
        handler.postDelayed(runnable, 5000);
        Log.d("SongViewActivity", "startCount");
    }

    public void stopCount() {
        handler.removeCallbacks(runnable);
    }
}
