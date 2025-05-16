package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.activities.AddSongToPlaylistActivity;
import com.example.spotify_app.adapters.SongAdapter;
import com.example.spotify_app.databinding.BottomSheetBinding;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.Check;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.SongLikedResponse;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    BottomSheetBinding binding;
    private Long songId;
    private String imageUrl;
    private String songName;
    private String artistName;
    private APIService apiService;

    SongAdapter songAdapter;
    private User user;
    private OnSongActionListener songActionListener;


    public void setOnSongActionListener(OnSongActionListener listener) {
        this.songActionListener = listener;
    }

    public BottomSheetDialog() { }

    public void setContent(Long songId, String imageUrl, String songName, String artistName) {
        this.songId = songId;
        this.imageUrl = imageUrl;
        this.songName = songName;
        this.artistName = artistName;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        binding = BottomSheetBinding.bind(view);
        user = SharePrefManagerUser.getInstance(getContext()).getUser();

        binding.itemSong.btnSongOption.setVisibility(View.GONE);

        if (songId != null && imageUrl != null && songName != null && artistName != null) {
            // Set content for item song
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(binding.itemSong.imSongAvt);
            binding.itemSong.tvSongTitle.setText(songName);
            binding.itemSong.tvSongArtist.setText(artistName);

            // Change menu item favourite for navigation view if song liked
            apiService = RetrofitClient.getRetrofit().create(APIService.class);
            apiService.isUserLikedSong(songId,(long) user.getId() ).enqueue(new Callback<SongLikedResponse>() {
                @Override
                public void onResponse(Call<SongLikedResponse> call, Response<SongLikedResponse> response) {
                    if (response.isSuccessful() && response.body().isData()) {
                        binding.navigationView.getMenu()
                                .findItem(R.id.menu_item_favourite)
                                .setTitle(getText(R.string.menu_item_remove_favourite))
                                .setIcon(R.drawable.ic_24dp_filled_favorite);
                        if (songActionListener != null) {
                            songActionListener.onSongLikedStatusChanged(songId, true);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SongLikedResponse> call, Throwable t) { }
            });

            // Handle navigation view item click
            binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.menu_item_favourite) {
                        // tạm sửa là id cố định (long) user.getId()

                        apiService.toggleLike(songId,(long)user.getId()).enqueue(new Callback<GenericResponse<Boolean>>() {
                            @Override
                            public void onResponse(Call<GenericResponse<Boolean>> call, Response<GenericResponse<Boolean>> response) {
                                if (response.isSuccessful()) {
                                    boolean isLiked = response.body().getData();
                                    
                                    // Cập nhật UI của BottomSheet
                                    if (isLiked) {
                                        Toast.makeText(requireContext(), getText(R.string.toast_added_song_to_favourite), Toast.LENGTH_SHORT).show();
                                        binding.navigationView.getMenu()
                                                .findItem(R.id.menu_item_favourite)
                                                .setTitle(getText(R.string.menu_item_remove_favourite))
                                                .setIcon(R.drawable.ic_24dp_filled_favorite);
                                        if (songActionListener != null) {
                                            songActionListener.onSongLikedStatusChanged(songId, true);
                                        }
                                    } else {
                                        Toast.makeText(requireContext(), getText(R.string.toast_removed_song_to_favourite), Toast.LENGTH_SHORT).show();
                                        binding.navigationView.getMenu()
                                                .findItem(R.id.menu_item_favourite)
                                                .setTitle(getText(R.string.menu_item_add_favourite))
                                                .setIcon(R.drawable.ic_24dp_outline_favorite);
                                        if (songActionListener != null) {
                                            songActionListener.onSongLikedStatusChanged(songId, false);
                                        }
                                    }
                                    

                                    
                                    // Đóng BottomSheet sau khi xử lý xong
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        BottomSheetDialog.this.dismiss();
                                    }, 300);
                                }
                            }

                            @Override
                            public void onFailure(Call<GenericResponse<Boolean>> call, Throwable t) {
                                Log.d("test", "Lỗi mạng: " + t.getMessage());
                                t.printStackTrace();
                            }
                        });

                    } else if (itemId == R.id.menu_item_add_playlist) {
                        Intent intent = new Intent(getContext(), AddSongToPlaylistActivity.class);
                        intent.putExtra("SongId", songId);
                        startActivity(intent);

                    } else if (itemId == R.id.menu_item_remove_playlist) {
                        Intent topicIntent = getActivity().getIntent();
                        String topic = topicIntent.getStringExtra("topic");
                        try {
                            int number = Integer.parseInt(topic);
                            apiService.deleteSongFromPlaylist((long) number, songId).enqueue(new Callback<ResponseMessage>() {
                                @Override
                                public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                    if (response.isSuccessful()) {
                                        if (songActionListener != null) {
                                            songActionListener.onSongDeleted(songId);
                                        }
                                        Toast.makeText(getContext(), getText(R.string.toast_removed_song_from_playlist), Toast.LENGTH_SHORT).show();
                                        BottomSheetDialog.this.dismiss();

                                        Intent intent = getActivity().getIntent();
                                        getActivity().finish();
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseMessage> call, Throwable t) {
                                    // xử lý lỗi nếu cần
                                }
                            });
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), getText(R.string.toast_forbiden), Toast.LENGTH_SHORT).show();
                            BottomSheetDialog.this.dismiss();
                        }
                    }

                    return false;
                }

            });
        }

        return view;
    }

    public interface OnSongActionListener {
        void onSongLikedStatusChanged(Long songId, boolean isLiked);
        void onSongDeleted(Long songId);
    }
}
