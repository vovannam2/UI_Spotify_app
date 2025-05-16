package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.spotify_app.R;
import com.example.spotify_app.activities.CreatePlaylistActivity;
import com.example.spotify_app.activities.TopicActivity;
import com.example.spotify_app.adapters.PlaylistAdapter;
import com.example.spotify_app.databinding.FragmentUserPlaylistBinding;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.ListPlaylistResponse;
import com.example.spotify_app.models.Playlist;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPlaylistFragment extends Fragment {

    FragmentUserPlaylistBinding binding;
    RecyclerView recyclerView;
    PlaylistAdapter adapter;
    List<Playlist> playlists = new ArrayList<>();

    LinearLayout linearLayoutAddToLibrary;

    User user;
    public UserPlaylistFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserPlaylistBinding.inflate(inflater, container, false);
        user = SharePrefManagerUser.getInstance(this.getContext()).getUser();

        // Set text for tvAddToLibrary in include layout
        binding.linearLayoutAddToLibary.tvAddToLibrary.setText(R.string.label_add_new_playlist);

        // Set onClickListener for linearLayoutAddToLibrary
        linearLayoutAddToLibrary = binding.linearLayoutAddToLibary.getRoot();
        linearLayoutAddToLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), getString(R.string.label_add_new_playlist), Toast.LENGTH_SHORT).show();
                Log.d("nam","võ văn nam");
                Intent intent = new Intent(getContext(), CreatePlaylistActivity.class);
                startActivity(intent);
            }
        });

        // Bind recyclerView and adapter
        recyclerView = binding.rvUserPlaylist;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        RecyclerView.ItemDecoration itemDecoration = new BottomOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.bottom_offset));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);

        getPlaylistByIdUser();
        refreshFavouriteSongsIfNeeded();
        return binding.getRoot();
    }

    private void getPlaylistByIdUser() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getPlaylistByIdUser(user.getId()).enqueue(new Callback<ListPlaylistResponse>() {
            @Override
            public void onResponse(Call<ListPlaylistResponse> call, Response<ListPlaylistResponse> response) {
                if (response.isSuccessful()) {
                    playlists = response.body().getData();
                    adapter = new PlaylistAdapter(getContext(), playlists);
                    adapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Playlist playlist) {
                            Intent intent = new Intent(getContext(), TopicActivity.class);
                            intent.putExtra("topic", String.valueOf(playlist.getIdPlaylist()));
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ListPlaylistResponse> call, Throwable t) {
            }
        });
    }

    private void refreshFavouriteSongsIfNeeded() {
        binding.swipeRefreshLayoutUserPlaylists.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlaylistByIdUser();
                binding.swipeRefreshLayoutUserPlaylists.setRefreshing(false);
            }
        });
    }
}