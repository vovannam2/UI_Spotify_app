package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.adapters.PlaylistAddToLibraryAdapter;
import com.example.spotify_app.databinding.ActivityAddSongToPlaylistBinding;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.ListPlaylistResponse;
import com.example.spotify_app.models.Playlist;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSongToPlaylistActivity extends AppCompatActivity {
    ActivityAddSongToPlaylistBinding binding;
    List<Playlist> playlists;
    RecyclerView recyclerView;
    PlaylistAddToLibraryAdapter adapter;
    APIService apiService;
    User user = SharePrefManagerUser.getInstance(this).getUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddSongToPlaylistBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());
        recyclerView = binding.rvYourPlaylists;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new BottomOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.bottom_offset)));
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        getUserPlaylists();
        addSongToPlaylist();
        clearCheckedSongsIfNeeded();
    }

    private void getUserPlaylists() {
        apiService.getPlaylistByIdUser(user.getId()).enqueue(new Callback<ListPlaylistResponse>() {
            @Override
            public void onResponse(Call<ListPlaylistResponse> call, Response<ListPlaylistResponse> response) {
                if (response.isSuccessful()) {
                    playlists = response.body().getData();
                    adapter = new PlaylistAddToLibraryAdapter(getApplicationContext(), playlists);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ListPlaylistResponse> call, Throwable t) {

            }
        });
    }
    private void addSongToPlaylist() {
        binding.fabAddToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long songId = getIntent().getLongExtra("SongId", 0);
                for (int i = 0; i < adapter.getCheckedPlaylistIds().size(); i++) {
                    apiService.addSongToPlaylist(adapter.getCheckedPlaylistIds().get(i), songId).enqueue(new Callback<ResponseMessage>() {
                        @Override
                        public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                            if (response.isSuccessful() && response.body().getData().equals(Boolean.FALSE)) {
                                Toast.makeText(AddSongToPlaylistActivity.this, getText(R.string.toast_song_already_in_playlist), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseMessage> call, Throwable t) { }
                    });
                    if (i == adapter.getCheckedPlaylistIds().size() - 1) {
                        Intent intent = new Intent(AddSongToPlaylistActivity.this, MainActivity.class);
                        intent.putExtra("fragmentId", R.id.fragment_library);
                        startActivity(intent);
                        Toast.makeText(AddSongToPlaylistActivity.this, getText(R.string.toast_added_song_to_playlist), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private void clearCheckedSongsIfNeeded() {
        binding.tvClearChoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clearAllCheckedPlaylists();
            }
        });
    }
}