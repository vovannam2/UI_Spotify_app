package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.spotify_app.R;
import com.example.spotify_app.activities.AddFavouriteSongsActivity;
import com.example.spotify_app.activities.SongDetailActivity;
import com.example.spotify_app.adapters.SongAdapter;
import com.example.spotify_app.databinding.FragmentUserFavouriteBinding;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFavouriteFragment extends Fragment implements SongAdapter.OnItemClickListener{
    FragmentUserFavouriteBinding binding;
    RecyclerView songRecyclerView;
    SongAdapter songAdapter;
    List<Song> favouriteSongs;
    LinearLayout linearLayoutAddToLibrary;
    APIService apiService;
    User user;
    ExoPlayerQueue exoPlayerQueue;
    public UserFavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserFavouriteBinding.inflate(inflater, container, false);
        user = SharePrefManagerUser.getInstance(this.getContext()).getUser();

        // Set text for tvAddToLibrary in include layout
        binding.linearLayoutAddToLibary.tvAddToLibrary.setText(R.string.label_add_new_favourite);

        exoPlayerQueue = ExoPlayerQueue.getInstance();
        // Set onClickListener for linearLayoutAddToLibrary
        linearLayoutAddToLibrary = binding.linearLayoutAddToLibary.getRoot();
        linearLayoutAddToLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddFavouriteSongsActivity.class);
                startActivity(intent);
            }
        });

        // Bind recyclerView and adapter
        songRecyclerView = binding.rvUserFavourites;
        songRecyclerView.setAdapter(songAdapter);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerView.ItemDecoration itemDecoration = new BottomOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.bottom_offset));
        songRecyclerView.addItemDecoration(itemDecoration);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        getSongLikedByIdUser();
        refreshFavouriteSongsIfNeeded();
        return binding.getRoot();
    }

    private void getSongLikedByIdUser() {
        favouriteSongs = new ArrayList<>();
        songAdapter = new SongAdapter(getContext(), favouriteSongs, this);
        apiService.getSongLikedByIdUser(user.getId()).enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                if (response.isSuccessful()) {
                    favouriteSongs.addAll(response.body().getData());
                    songRecyclerView.setAdapter(songAdapter);
                    songAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
            }
        });
    }

    private void refreshFavouriteSongsIfNeeded() {
        binding.swipeRefreshLayoutUserFavourites.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                favouriteSongs.clear();
                getSongLikedByIdUser();
                binding.swipeRefreshLayoutUserFavourites.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSongClick(int position) {
        exoPlayerQueue.clear();
        exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(favouriteSongs));
        exoPlayerQueue.setCurrentPosition(position);

        Intent intent = new Intent(getContext(), SongDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayPlaylistClick(List<Song> songList) {

    }
}