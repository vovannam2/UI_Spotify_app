package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotify_app.R;
import com.example.spotify_app.adapters.SongAddToLibraryAdapter;
import com.example.spotify_app.databinding.ActivityCreatePlaylistBinding;
import com.example.spotify_app.databinding.FragmentLibraryBinding;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Playlist;
import com.example.spotify_app.models.PlaylistRequest;
import com.example.spotify_app.models.PlaylistResponse;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePlaylistActivity extends AppCompatActivity {
    ActivityCreatePlaylistBinding binding;
    RecyclerView recyclerView;
    SongAddToLibraryAdapter adapter;
    List<Song> songs;
    APIService apiService;
    Handler handler;
    Runnable runnable;
    User user;

    TextInputLayout playlistNameLayout;
    TextInputEditText edtPlaylistName;
    ExtendedFloatingActionButton fabCreatePlaylist;

    FragmentLibraryBinding libraryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        /*setContentView(R.layout.activity_create_playlist);*/
        binding = ActivityCreatePlaylistBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());
        user = SharePrefManagerUser.getInstance(this).getUser();

        // Binding views
        recyclerView = binding.rvRecommendedSongs;
        playlistNameLayout = binding.playlistNameLayout;
        edtPlaylistName = binding.edtPlaylistName;
        edtPlaylistName.requestFocus();
        fabCreatePlaylist = binding.fabCreatePlaylist;

        adapter = new SongAddToLibraryAdapter(getApplicationContext(), songs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new BottomOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.bottom_offset)));
        getAllSongs();
        checkPlaylistName();
        createPlaylist();
        clearCheckedSongsIfNeeded();
    }

    private void getAllSongs() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getAllSongs().enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                if (response.isSuccessful()) {
                    songs = response.body().getData();
                    Collections.shuffle(songs);
                    adapter = new SongAddToLibraryAdapter(getApplicationContext(), songs);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {

            }
        });

    }

    private void checkPlaylistName() {
        fabCreatePlaylist.setEnabled(false);
        handler = new Handler();
        edtPlaylistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        String playlistName = edtPlaylistName.getText().toString();
                        if (playlistName.isEmpty()) {
                            fabCreatePlaylist.setEnabled(false);
                            playlistNameLayout.setError(getText(R.string.error_required_field));
                        } else {
                            apiService.isPlaylistNameExists(playlistName).enqueue(new Callback<ResponseMessage>() {
                                @Override
                                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body().getData().equals(Boolean.valueOf(true))) {
                                            fabCreatePlaylist.setEnabled(false);
                                            playlistNameLayout.setError(getText(R.string.error_playlist_name_exists));
                                        } else {
                                            fabCreatePlaylist.setEnabled(true);
                                            playlistNameLayout.setError(null);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseMessage> call, Throwable t) {

                                }
                            });
                        }
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        });
    }

    private void createPlaylist() {
            fabCreatePlaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlaylistRequest playlistRequest = new PlaylistRequest();
                    playlistRequest.setIdUser(user.getId());
                    playlistRequest.setName(edtPlaylistName.getText().toString());
                    playlistRequest.setSongIds(adapter.getCheckedSongIds());
                    apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    apiService.createPlaylist(playlistRequest).enqueue(new Callback<PlaylistResponse>() {
                        @Override
                        public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                            if (response.isSuccessful()) {
                                //Log.d("test2","Lan anh2");
                                Intent intent = new Intent(CreatePlaylistActivity.this, MainActivity.class);
                                intent.putExtra("fragmentId", R.id.fragment_library);
                                startActivity(intent);

                                libraryBinding = FragmentLibraryBinding.inflate(getLayoutInflater(), null, false);
                                TabLayout tabLayout = libraryBinding.libraryTabLayout;
                                int tabIndex = 1;
                                TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
                                if (tab != null) {
                                    tab.select();
                                }

                                finish();
                                Toast.makeText(CreatePlaylistActivity.this, getText(R.string.toast_created_playlist), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                            Log.d("test2", "Error type: " + t.getClass().getSimpleName());
                            Log.d("test2", "Error message: " + t.getMessage());
                            Log.d("test2", "Is request executed: " + call.isExecuted());
                            Log.d("test2", "Is request canceled: " + call.isCanceled());
                            t.printStackTrace();
                        }
                    });
                }
            });
    }

    private void clearCheckedSongsIfNeeded() {
        binding.tvClearChoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clearAllCheckedSongs();
            }
        });
    }
}