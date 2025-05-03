package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongResponse;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Bai_Hat_Hot extends Fragment {
    View view;
    RecyclerView recyclerViewBaiHatHot;
    private ExoPlayerQueue exoPlayerQueue;
    BaiHatHotAdapter baiHatHotAdapter;

    List<Song> songList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        view = inflater.inflate(R.layout.fragment_bai_hat_hot, container, false);
        viewBinding();
        getData();
        return view;
    }

    private void viewBinding() {
        recyclerViewBaiHatHot = view.findViewById(R.id.recyclerViewBaiHatHot);
    }
    private final BaiHatHotAdapter.OnItemClickListener songhotclick = new BaiHatHotAdapter.OnItemClickListener() {
        @Override
        public void onSongClick(int position) {


            // Chuyển songTrendList thành MediaItem cho ExoPlayer
            exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));

            // Thiết lập vị trí bài hát cần phát
            exoPlayerQueue.setCurrentPosition(position);


            // Gửi thông tin bài hát vào SongDetailActivity
            Intent intent = new Intent(getContext(), SongDetailActivity.class);


            // Bắt đầu Activity chi tiết bài hát
            startActivity(intent);
        }

        @Override
        public void onPlayPlaylistClick(List<Song> songList) {

        }
    };

    private void getData() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);

        // Lấy trang đầu tiên (0), mỗi trang 10 bài
        Call<GenericResponse<SongResponse>> callback = apiService.getMostViewSong(0, 10,"views");

        callback.enqueue(new Callback<GenericResponse<SongResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<SongResponse>> call, Response<GenericResponse<SongResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    songList = response.body().getData().getContent();
                    Log.d("API_RESPONSE", "Số lượng bài hát: " + songList.size());

                    for (Song song : songList) {
                        Log.d("API_RESPONSE", "Tên bài hát: " + song.getName());
                    }

                    baiHatHotAdapter = new BaiHatHotAdapter(getActivity(), new ArrayList<>(songList),songhotclick);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerViewBaiHatHot.setLayoutManager(linearLayoutManager);
                    recyclerViewBaiHatHot.setAdapter(baiHatHotAdapter);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<SongResponse>> call, Throwable t) {
                Log.e("API_ERROR", "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }



    @Override
    public void onResume() {
        getData();
        super.onResume();
    }
}
