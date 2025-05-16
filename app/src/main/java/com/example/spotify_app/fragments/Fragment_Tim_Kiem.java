package com.example.spotify_app.fragments;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.ArtistActivity;
import com.example.spotify_app.activities.SongDetailActivity;
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

import com.example.spotify_app.adapters.SearchHistoryAdapter;
import com.example.spotify_app.internals.SharePrefSearchHistory;

public class Fragment_Tim_Kiem extends Fragment {

    private ActivitySearchBinding binding;
    private ExoPlayerQueue exoPlayerQueue;
    private APIService apiService;
    private List<Song> songList;
    private List<String> historyList;
    private SearchHistoryAdapter historyAdapter;
    private SharePrefSearchHistory searchHistory;

    // Adapter click listeners
    private final SongAdapter.OnItemClickListener songhotclick = new SongAdapter.OnItemClickListener() {
        @Override
        public void onSongClick(int position) {
            if (songList == null || songList.isEmpty()) return;
            exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));
            exoPlayerQueue.setCurrentPosition(position);
            Intent intent = new Intent(requireContext(), SongDetailActivity.class);
            startActivity(intent);
        }
        @Override
        public void onPlayPlaylistClick(List<Song> songList) { /* Implement if needed */ }
    };

    private final ArtistAdapter.OnItemClickListener artistItemClick = new ArtistAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Artist artist) {
            Intent intent = new Intent(requireContext(), ArtistActivity.class);
            intent.putExtra("artistId", artist.getIdUser());
            startActivity(intent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ActivitySearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public static Fragment_Tim_Kiem newInstance() {
        return new Fragment_Tim_Kiem();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exoPlayerQueue = ExoPlayerQueue.getInstance();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        
        // Khởi tạo search history
        searchHistory = new SharePrefSearchHistory(requireContext());
        historyList = new ArrayList<>(searchHistory.getSearchHistory());
        setupSearchHistoryRecyclerView();

        // Setup RecyclerViews
        binding.recyclerViewSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewAlbums.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewPlaylists.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewArtists.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // Tab change listener
        binding.chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String query = binding.edittext.getText().toString();
            searchByTab(query);
        });

        // Search action
        binding.edittext.setOnEditorActionListener((textView, i, keyEvent) -> {
            String query = textView.getText().toString();
            if (!query.trim().isEmpty()) {
                searchHistory.addSearchQuery(query);
                updateHistoryList();
            }
            searchByTab(query);
            binding.edittext.clearFocus();
            hideKeyboard(binding.edittext);
            return true;
        });

        binding.edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.clearIcon.setVisibility(s.toString().isEmpty() ? View.GONE : View.VISIBLE);
                if (s.toString().isEmpty()) {
                    showHistory();
                }
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        binding.clearIcon.setOnClickListener(v -> {
            binding.edittext.setText("");
            showHistory();
        });

        // Hiển thị lịch sử tìm kiếm khi mới vào tab
        if (binding.edittext.getText().toString().isEmpty()) {
            showHistory();
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setupSearchHistoryRecyclerView() {
        historyAdapter = new SearchHistoryAdapter(
            historyList,
            query -> {
                binding.edittext.setText(query);
                searchByTab(query);
            },
            query -> {
                searchHistory.removeSearchQuery(query);
                historyList.remove(query);
                historyAdapter.updateData(historyList);
            }
        );

        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void updateHistoryList() {
        historyList.clear();
        historyList.addAll(searchHistory.getSearchHistory());
        historyAdapter.updateData(historyList);
    }

    private void showHistory() {
        binding.recyclerViewHistory.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);
    }

    private void hideHistory() {
        binding.recyclerViewHistory.setVisibility(View.GONE);
        binding.nestedScrollView.setVisibility(View.VISIBLE);
    }

    private void searchByTab(String query) {
        if (query == null || query.trim().isEmpty()) {
            showHistory();
            return;
        }

        hideHistory();
        int checkedChipId = binding.chipGroup.getCheckedChipId();
        if (checkedChipId == R.id.chip_all) {
            showAllLists();
            searchAll(query);
        } else if (checkedChipId == R.id.chip_song) {
            showOnlyList(binding.recyclerViewSongs, binding.textViewSongs);
            searchSongs(query);
        } else if (checkedChipId == R.id.chip_albums) {
            showOnlyList(binding.recyclerViewAlbums, binding.textViewAlbums);
            searchAlbums(query);
        } else if (checkedChipId == R.id.chip_playlists) {
            showOnlyList(binding.recyclerViewPlaylists, binding.textViewPlaylists);
            searchPlaylists(query);
        } else if (checkedChipId == R.id.chip_artists) {
            showOnlyList(binding.recyclerViewArtists, binding.textViewArtists);
            searchArtists(query);
        }
    }

    private void showAllLists() {
        binding.recyclerViewSongs.setVisibility(View.VISIBLE);
        binding.recyclerViewAlbums.setVisibility(View.VISIBLE);
        binding.recyclerViewPlaylists.setVisibility(View.VISIBLE);
        binding.recyclerViewArtists.setVisibility(View.VISIBLE);
        binding.textViewSongs.setVisibility(View.VISIBLE);
        binding.textViewAlbums.setVisibility(View.VISIBLE);
        binding.textViewPlaylists.setVisibility(View.VISIBLE);
        binding.textViewArtists.setVisibility(View.VISIBLE);
    }

    private void showOnlyList(View visibleList, View visibleTitle) {
        binding.recyclerViewSongs.setVisibility(visibleList == binding.recyclerViewSongs ? View.VISIBLE : View.GONE);
        binding.recyclerViewAlbums.setVisibility(visibleList == binding.recyclerViewAlbums ? View.VISIBLE : View.GONE);
        binding.recyclerViewPlaylists.setVisibility(visibleList == binding.recyclerViewPlaylists ? View.VISIBLE : View.GONE);
        binding.recyclerViewArtists.setVisibility(visibleList == binding.recyclerViewArtists ? View.VISIBLE : View.GONE);
        binding.textViewSongs.setVisibility(visibleTitle == binding.textViewSongs ? View.VISIBLE : View.GONE);
        binding.textViewAlbums.setVisibility(visibleTitle == binding.textViewAlbums ? View.VISIBLE : View.GONE);
        binding.textViewPlaylists.setVisibility(visibleTitle == binding.textViewPlaylists ? View.VISIBLE : View.GONE);
        binding.textViewArtists.setVisibility(visibleTitle == binding.textViewArtists ? View.VISIBLE : View.GONE);
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
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchSongs(String query) {
        fetchAndSetAdapter(
                apiService.getSearchMusicList(query),
                binding.recyclerViewSongs,
                data -> {
                    songList = data;
                    return new SongAdapter(requireContext(), data, songhotclick);
                },
                "Lỗi khi tìm kiếm bài hát"
        );
    }

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

    private void searchArtists(String query) {
        fetchAndSetAdapter(
                apiService.searchArtist(query),
                binding.recyclerViewArtists,
                data -> new ArtistAdapter(requireContext(), data, artistItemClick),
                "Lỗi khi tìm kiếm nghệ sĩ"
        );
    }
}