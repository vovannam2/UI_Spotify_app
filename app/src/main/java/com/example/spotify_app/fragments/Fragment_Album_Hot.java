package com.example.spotify_app.fragments;

import static androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.spotify_app.activities.TopicActivity;
import com.example.spotify_app.adapters.AlbumAdapter;
import com.example.spotify_app.models.Album;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.SongResponse;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Album_Hot extends Fragment {
    View view;
    RecyclerView recyclerViewAlbum;
    TextView tvXemThemAlbum;
    AlbumAdapter albumAdapter;

    private List<Album> listAlbum = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_album_hot, container, false);
        bindingView();
        init();
        getData();
        return view;
    }

    private void init() {
        tvXemThemAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TopicActivity.class);
                intent.putExtra("topic", "topArtist");
                startActivity(intent);
            }
        });
    }

    private void bindingView() {
        recyclerViewAlbum = view.findViewById(R.id.recylerViewAlbumHot);
        tvXemThemAlbum = view.findViewById(R.id.tvXemThemAlbum);
    }

    private void getData() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        albumAdapter = new AlbumAdapter(getActivity(), listAlbum, new AlbumAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(Album album) {
                Intent intent = new Intent(getContext(), TopicActivity.class);
                intent.putExtra("albumId", String.valueOf(album.getIdAlbum()));
                intent.putExtra("topic", "1");
                startActivity(intent);
            }
        });
        // Gọi API với GenericResponse
        Call<GenericResponse<List<Album>>> callback = apiService.getAlbumHot();
        callback.enqueue(new Callback<GenericResponse<List<Album>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Album>>> call, Response<GenericResponse<List<Album>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listAlbum.clear();
                    listAlbum.addAll(response.body().getData());
                    albumAdapter.notifyDataSetChanged();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(HORIZONTAL);
                    recyclerViewAlbum.setLayoutManager(linearLayoutManager);
                    recyclerViewAlbum.setAdapter(albumAdapter);
                }
                else {
                    // Xử lý nếu response không thành công
                    Log.e("onResponse", "Response không thành công: " + response.message());
                    Toast.makeText(getActivity(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<GenericResponse<List<Album>>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
