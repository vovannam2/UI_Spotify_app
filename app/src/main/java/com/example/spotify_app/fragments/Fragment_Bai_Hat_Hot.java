package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.SongDetailActivity;
import com.example.spotify_app.adapters.BaiHatHotAdapter;
import com.example.spotify_app.adapters.SongAdapter;
import com.example.spotify_app.adapters.songbxhadapter;
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongResponse;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.MaterialArcMotion;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Bai_Hat_Hot extends Fragment {
    View view;
    RecyclerView recyclerViewBaiHatHot;
    private ExoPlayerQueue exoPlayerQueue;
    songbxhadapter SongAdapter;
    List<Song> songList;
    private APIService apiService;
    User user;

    MaterialButton btnLoadMore;

    int page = 0, totalPages;
    boolean isLoading = false, isLastPage = false, isShuffle = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        view = inflater.inflate(R.layout.fragment_bai_hat_hot, container, false);
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        user = SharePrefManagerUser.getInstance(requireContext()).getUser();
        songList = new ArrayList<>();
        viewBinding();
        getData();
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading && !isLastPage) {
                    isLoading = true;
                    loadNextPage();
                }
            }
        });
        return view;
    }

    private void viewBinding() {
        recyclerViewBaiHatHot = view.findViewById(R.id.recyclerViewBaiHatHot);
        btnLoadMore = view.findViewById(R.id.btn_viewmore_newsongs);
    }

    private final songbxhadapter.OnItemClickListener songhotclick = new songbxhadapter.OnItemClickListener() {
        @Override
        public void onSongClick(int position) {
            if (!isAdded()) return; // Kiểm tra fragment có được gắn vào activity không

            // Chuyển songTrendList thành MediaItem cho ExoPlayer
            exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));

            // Thiết lập vị trí bài hát cần phát
            exoPlayerQueue.setCurrentPosition(position);

            // Gửi thông tin bài hát vào SongDetailActivity
            Intent intent = new Intent(requireContext(), SongDetailActivity.class);
            startActivity(intent);
        }

        @Override
        public void onPlayPlaylistClick(List<Song> songList) {
            // Implement if needed
        }
    };

    private void getData() {
        if (!isAdded()) return; // Kiểm tra fragment có được gắn vào activity không
        SongAdapter = new songbxhadapter(requireContext(), songList, songhotclick);
        fetchSongs(apiService.getMostViewSong(0, 6, "views"));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewBaiHatHot.setLayoutManager(linearLayoutManager);
        recyclerViewBaiHatHot.setAdapter(SongAdapter);



        apiService.getSongLikedByIdUser(user.getId()).enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Long> likedSongIds = new ArrayList<>();
                    for (Song song : response.body().getData()) {
                        likedSongIds.add(song.getIdSong());
                    }
                    // Thêm log để kiểm tra
                    Log.d("ArtistActivity", "Liked Song IDs: " + likedSongIds);
                    // Gọi phương thức adapter để cập nhật hình ảnh trái tim
                    SongAdapter.setLikedSongIds(likedSongIds);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
                Log.e("API_FAILURE", "Failed to fetch liked songs: " + t.getMessage());
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
       /* if (isAdded()) { // Chỉ gọi getData() nếu fragment đã được gắn vào activity
            getData();
        }*/
    }

    private void fetchSongs(Call<GenericResponse<SongResponse>> call) {
        call.enqueue(new Callback<GenericResponse<SongResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<SongResponse>> call, Response<GenericResponse<SongResponse>> response) {
                if (response.isSuccessful()) {
                    List<Song> newList = response.body().getData().getContent();
                    totalPages = response.body().getData().getTotalPages();
                    songList.addAll(newList);
                    page++;
                    Log.d("test api", "Page hiện tại: " + page);
                    SongAdapter.notifyDataSetChanged();

                    if (page >= totalPages) {
                        isLastPage = true;
                        btnLoadMore.setVisibility(View.GONE);
                    }
                }
                isLoading = false; // ✅ Chỉ tắt loading sau khi xử lý xong
            }

            @Override
            public void onFailure(Call<GenericResponse<SongResponse>> call, Throwable t) {
                Log.d("ArtistActivity", "onFailure: " + t.getMessage());
                isLoading = false; // ✅ Tắt luôn nếu lỗi xảy ra
            }
        });
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page < totalPages) {
                    APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    fetchSongs(apiService.getMostViewSong(page,6 , "views"));
                } else {
                    isLoading = false;
                    isLastPage = true;
                    btnLoadMore.setVisibility(View.GONE);
                }
            }
        }, 500); // delay 500ms tạo cảm giác "loading"
    }
}
