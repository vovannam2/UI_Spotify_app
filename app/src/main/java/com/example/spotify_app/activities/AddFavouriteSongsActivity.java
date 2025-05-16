package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotify_app.R;
import com.example.spotify_app.adapters.SongAddToLibraryAdapter;
import com.example.spotify_app.databinding.ActivityAddFavouriteSongsBinding;
import com.example.spotify_app.databinding.FragmentLibraryBinding;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongLikedRequest;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.google.android.material.tabs.TabLayout;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFavouriteSongsActivity extends AppCompatActivity {
    ActivityAddFavouriteSongsBinding binding;
    FragmentLibraryBinding libraryBinding;
    RecyclerView recyclerView;
    SongAddToLibraryAdapter adapter;
    List<Song> songs;
    APIService apiService;
    User user = SharePrefManagerUser.getInstance(this).getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFavouriteSongsBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());
        
        // Binding views
        recyclerView = binding.rvRecommendedSongs;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new BottomOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.bottom_offset)));
        getSongNotInFavourite();
        addSongToFavourite();
        clearCheckedSongsIfNeeded();
    }

    private void getSongNotInFavourite() {
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getNotLikedSongsByIdUser(user.getId()).enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                if (response.isSuccessful()) {
                    songs = response.body().getData();
                    Collections.shuffle(songs);
                    adapter = new SongAddToLibraryAdapter(getApplicationContext(), songs);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {

            }
        });
    }

    public void addSongToFavourite() {
        binding.fabAddToFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SongLikedRequest songLikedRequest = new SongLikedRequest();
                songLikedRequest.setIdUser((long) user.getId());
                songLikedRequest.setSongIds(adapter.getCheckedSongIds());
                apiService = RetrofitClient.getRetrofit().create(APIService.class);
                apiService.addSongsToFavourite(songLikedRequest).enqueue(new Callback<ResponseMessage>() {
                    @Override
                    public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                        if (response.isSuccessful()) {
                            Intent intent = new Intent(AddFavouriteSongsActivity.this, MainActivity.class);
                            intent.putExtra("fragmentId", R.id.fragment_library);
                            startActivity(intent);

                            libraryBinding = FragmentLibraryBinding.inflate(getLayoutInflater(), null, false);
                            TabLayout tabLayout = libraryBinding.libraryTabLayout;
                            int tabIndex = 1;
                            TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
                            if (tab != null) {
                                tab.select();
                            }
                            Log.d("test","Lan anh");
                            Toast.makeText(AddFavouriteSongsActivity.this, getText(R.string.toast_added_song_to_favourite), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMessage> call, Throwable t) {

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