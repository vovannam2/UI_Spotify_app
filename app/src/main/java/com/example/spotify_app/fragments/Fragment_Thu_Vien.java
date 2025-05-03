package com.example.spotify_app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Thu_Vien extends Fragment {
    View view;
    TextView tvCuaToi;
    RecyclerView recyclerViewSearchMusic;
    private SharedPreferences prefs;
    APIService apiService ;

    List<Song> listsong;

    private ExoPlayerQueue exoPlayerQueue;

    BaiHatHotAdapter baiHatHotAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeForSearch);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        view = localInflater.inflate(R.layout.fragment_thu_vien, container, false);
        prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        viewBinding();
        getData();
        return view;
    }

    private void viewBinding() {
        tvCuaToi = view.findViewById(R.id.tvCuaToi);
        recyclerViewSearchMusic = view.findViewById(R.id.recyclerViewSearchThuVienMusic);
        setHasOptionsMenu(true);
    }

    private final BaiHatHotAdapter.OnItemClickListener songthuvienclick = new BaiHatHotAdapter.OnItemClickListener() {
        @Override
        public void onSongClick(int position) {


            // Chuyển songTrendList thành MediaItem cho ExoPlayer
            exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(listsong));

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
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        final String idUser = prefs.getString("idUser", "0");
        Call<GenericResponse<List<Song>>> callback = apiService.getSongLikedByIdUser(2);

        System.out.println("chua cap nhat lai du lieu");

        callback.enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                System.out.println("cap nhat lai du lieu");
                listsong = response.body().getData();
                ArrayList<Song> baiHatArrayList = new ArrayList<> (listsong);
                BaiHatHotAdapter baiHatHotAdapter = new BaiHatHotAdapter(getActivity(), baiHatArrayList,songthuvienclick);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerViewSearchMusic.setLayoutManager(linearLayoutManager);
                recyclerViewSearchMusic.setAdapter(baiHatHotAdapter);
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }
    public static Fragment_Thu_Vien newInstance() {
        Fragment_Thu_Vien fragment = new Fragment_Thu_Vien();
        return fragment;
    }
}
