package com.example.spotify_app.activities;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.adapters.ArtistAdapter;
import com.example.spotify_app.databinding.ActivitySearchBinding;
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.models.Album;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.example.spotify_app.adapters.SongAdapter;
import com.example.spotify_app.adapters.AlbumAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    private ExoPlayerQueue exoPlayerQueue;
    private final String TAG = "SearchActivity";
    private APIService apiService;
    private RecyclerView recyclerViewSongs, recyclerViewAlbums, recyclerViewPlaylists, recyclerViewArtists;
    private TextView textViewSongs, textViewAlbums, textViewPlaylists, textViewArtists;
    private List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*OverScrollDecoratorHelper.setUpOverScroll(binding.hscrollview);*/

        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        recyclerViewSongs = findViewById(R.id.recyclerViewSongs);
        recyclerViewAlbums = findViewById(R.id.recyclerViewAlbums);
        recyclerViewPlaylists = findViewById(R.id.recyclerViewPlaylists);
        recyclerViewArtists = findViewById(R.id.recyclerViewArtists);

        recyclerViewSongs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        textViewSongs = findViewById(R.id.textViewSongs);
        textViewAlbums = findViewById(R.id.textViewAlbums);
        textViewPlaylists = findViewById(R.id.textViewPlaylists);
        textViewArtists = findViewById(R.id.textViewArtists);

        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String query = binding.edittext.getText().toString();
            searchByTab(query);
        });

        binding.edittext.setOnEditorActionListener((textView, i, keyEvent) -> {
            searchByTab(textView.getText().toString());
            binding.edittext.clearFocus();
            hideKeyboard(binding.edittext);
            return true;
        });

        binding.edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.clearIcon.setVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.clearIcon.setOnClickListener(v -> binding.edittext.setText(""));

    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void searchByTab(String query) {
        int checkedChipId = binding.chipGroup.getCheckedChipId();
        if (checkedChipId == R.id.chip_all) {
            showAllLists();
            searchAll(query);
        } else if (checkedChipId == R.id.chip_song) {
            showOnlyList(recyclerViewSongs, textViewSongs);
            searchSongs(query);
        } else if (checkedChipId == R.id.chip_albums) {
            showOnlyList(recyclerViewAlbums, textViewAlbums);
            searchAlbums(query);
        } else if (checkedChipId == R.id.chip_playlists) {
            showOnlyList(recyclerViewPlaylists, textViewPlaylists);
            searchPlaylists(query);
        } else if (checkedChipId == R.id.chip_artists) {
            showOnlyList(recyclerViewArtists, textViewArtists);
            searchArtists(query);
        }
    }

    private void showAllLists() {
        recyclerViewSongs.setVisibility(View.VISIBLE);
        recyclerViewAlbums.setVisibility(View.VISIBLE);
        recyclerViewPlaylists.setVisibility(View.VISIBLE);
        recyclerViewArtists.setVisibility(View.VISIBLE);
        textViewSongs.setVisibility(View.VISIBLE);
        textViewAlbums.setVisibility(View.VISIBLE);
        textViewPlaylists.setVisibility(View.VISIBLE);
        textViewArtists.setVisibility(View.VISIBLE);
    }

    private void showOnlyList(View visibleList, View visibleTitle) {
        recyclerViewSongs.setVisibility(visibleList == recyclerViewSongs ? View.VISIBLE : View.GONE);
        recyclerViewAlbums.setVisibility(visibleList == recyclerViewAlbums ? View.VISIBLE : View.GONE);
        recyclerViewPlaylists.setVisibility(visibleList == recyclerViewPlaylists ? View.VISIBLE : View.GONE);
        recyclerViewArtists.setVisibility(visibleList == recyclerViewArtists ? View.VISIBLE : View.GONE);
        textViewSongs.setVisibility(visibleTitle == textViewSongs ? View.VISIBLE : View.GONE);
        textViewAlbums.setVisibility(visibleTitle == textViewAlbums ? View.VISIBLE : View.GONE);
        textViewPlaylists.setVisibility(visibleTitle == textViewPlaylists ? View.VISIBLE : View.GONE);
        textViewArtists.setVisibility(visibleTitle == textViewArtists ? View.VISIBLE : View.GONE);
    }

    private void searchAll(String query) {
        searchSongs(query);
        searchAlbums(query);
        searchPlaylists(query);
        searchArtists(query);
    }

    private <T> void fetchAndSetAdapter(Call<GenericResponse<List<T>>> call, RecyclerView recyclerView, Function<List<T>, RecyclerView.Adapter> adapterFactory, String errorMsg) {
        call.enqueue(new Callback<GenericResponse<List<T>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<T>>> call, Response<GenericResponse<List<T>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<T> data = response.body().getData();
                    recyclerView.setAdapter(adapterFactory.apply(data));
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<List<T>>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void searchSongs(String query) {
        fetchAndSetAdapter(
                apiService.getSearchMusicList(query),
                recyclerViewSongs,
                data -> {
                    songList = data; // Gán kết quả từ API vào songList
                    return new SongAdapter(getApplicationContext(), data, songhotclick);
                },
                "Lỗi khi tìm kiếm bài hát"
        );
    }

    // 3. Sử dụng songList trong hàm onSongClick
    private final SongAdapter.OnItemClickListener songhotclick = new SongAdapter.OnItemClickListener() {
        @Override
        public void onSongClick(int position) {
            if (songList == null || songList.isEmpty()) {
                // Kiểm tra nếu songList không có dữ liệu
                return;
            }

            // Chuyển songList thành MediaItem cho ExoPlayer
            exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));

            // Thiết lập vị trí bài hát cần phát
            exoPlayerQueue.setCurrentPosition(position);

            // Gửi thông tin bài hát vào SongDetailActivity
            Intent intent = new Intent(getApplicationContext(), SongDetailActivity.class);
            startActivity(intent);
        }

        @Override
        public void onPlayPlaylistClick(List<Song> songList) {
            // Implement if needed
        }
    };

    private void searchAlbums(String query) {
        /*fetchAndSetAdapter(
            apiService.getSearchAlbums(query),
            recyclerViewAlbums,
            data -> new AlbumAdapter(data),
            "Lỗi khi tìm kiếm album"
        );*/
    }

    private void searchPlaylists(String query) {
        /*fetchAndSetAdapter(
            apiService.getSearchPlaylists(query),
            recyclerViewPlaylists,
            data -> new PlaylistAdapter(data),
            "Lỗi khi tìm kiếm playlist"
        );*/
    }
    private final ArtistAdapter.OnItemClickListener artistItemClick = new ArtistAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Artist artist) {
            Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);
            intent.putExtra("artistId", artist.getIdUser());
            startActivity(intent);
        }
    };
    private void searchArtists(String query) {
        fetchAndSetAdapter(
            apiService.searchArtist(query),
            recyclerViewArtists,
            data -> new ArtistAdapter(getApplicationContext(), data, artistItemClick),
            "Lỗi khi tìm kiếm nghệ sĩ"
        );
    }

    public void backPress(View view) {
        finish();
    }
}