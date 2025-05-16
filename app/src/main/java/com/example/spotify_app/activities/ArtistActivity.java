package com.example.spotify_app.activities;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.adapters.AlbumAdapter;
import com.example.spotify_app.adapters.SongAdapter;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.helpers.ArtistHelper;
import com.example.spotify_app.helpers.GradientHelper;
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.Album;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongResponse;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistActivity extends BaseActivity{

    private int artistId;
    ImageView coverPic;
    EditText tvPlaylistTitle;
    TextView tvPlaylistIntro;
    TextView tvPlaySongCount;
    RecyclerView rvListSong, rvListAlbum;
    View includeTopPlaylist, includeTopPlaylistOption, includeListSong, container;
    MaterialButton btnOption, btnShuffle, btnLoadMore ,btnPlayAll;
    APIService apiService;
    List<Song> songList;
    SongAdapter songAdapter;
    List<Album> albumList;
    AlbumAdapter albumAdapter;
    int page = 0, totalPages;
    boolean isLoading = false, isLastPage = false, isShuffle = false;
    private ExoPlayerQueue exoPlayerQueue;
    boolean isUserFollowedArtist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        EdgeToEdge.enable(this);
        initMiniPlayer();

        artistId = getIntent().getIntExtra("artistId", -1);
        if(artistId == -1){
            finish();
        }

        exoPlayerQueue = ExoPlayerQueue.getInstance();

        container = findViewById(R.id.layout_container);
        btnLoadMore = findViewById(R.id.btn_viewmore_newsongs);
        btnLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoading && !isLastPage) {
                    isLoading = true;
                    loadNextPage();
                }
            }
        });

        includeTopPlaylist = findViewById(R.id.included_top_playlist);
        coverPic = includeTopPlaylist.findViewById(R.id.imCoverPicture);
        tvPlaylistTitle = includeTopPlaylist.findViewById(R.id.edtPlaylistTitle);
        tvPlaylistIntro = includeTopPlaylist.findViewById(R.id.tvPlaylistIntro);
        tvPlaySongCount = includeTopPlaylist.findViewById(R.id.tvPlaylistSongCount);
        includeTopPlaylist.findViewById(R.id.btn_edit_name).setVisibility(View.GONE);
        tvPlaylistIntro.setVisibility(View.GONE);
        includeTopPlaylist.findViewById(R.id.btn_edit_name).setEnabled(false);
        tvPlaylistTitle.setEnabled(false);
        includeTopPlaylistOption = findViewById(R.id.included_top_playlist_option);
        btnOption = includeTopPlaylistOption.findViewById(R.id.btn_delete_playlist);

       btnPlayAll = includeTopPlaylistOption.findViewById(R.id.btnTopPlaylistOptionPlay);

        User user = SharePrefManagerUser.getInstance(this).getUser();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        // truyền tạm 1 userid();
        apiService.isFollowedArtist(user.getId(), artistId).enqueue(new Callback<GenericResponse<Boolean>>() {

            @Override
            public void onResponse(Call<GenericResponse<Boolean>> call, Response<GenericResponse<Boolean>> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData()) {
                        btnOption.setBackgroundColor(getResources().getColor(R.color.transparent));
                        btnOption.setTextColor(getResources().getColor(R.color.neutral0));
                        btnOption.setStrokeWidth(4);
                        btnOption.setText(R.string.followed);
                        isUserFollowedArtist = true;
                    } else {
                        btnOption.setBackgroundColor(getResources().getColor(R.color.primary));
                        btnOption.setTextColor(getResources().getColor(R.color.neutral5));
                        btnOption.setStrokeWidth(0);
                        btnOption.setText(R.string.follow);
                        isUserFollowedArtist = false;
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Boolean>> call, Throwable t) {

            }
        });

        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // tạm thời set user.getId()
                apiService.followArtist(user.getId(), artistId).enqueue(new Callback<GenericResponse<Boolean>>() {

                    @Override
                    public void onResponse(Call<GenericResponse<Boolean>> call, Response<GenericResponse<Boolean>> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getData()) {
                                btnOption.setBackgroundColor(getResources().getColor(R.color.transparent));
                                btnOption.setTextColor(getResources().getColor(R.color.neutral0));
                                btnOption.setStrokeWidth(4);
                                btnOption.setText(R.string.followed);
                                isUserFollowedArtist = true;
                                Toast.makeText(ArtistActivity.this, getString(R.string.toast_followed), Toast.LENGTH_SHORT).show();

                            } else {
                                btnOption.setBackgroundColor(getResources().getColor(R.color.primary));
                                btnOption.setTextColor(getResources().getColor(R.color.neutral5));
                                btnOption.setStrokeWidth(0);
                                btnOption.setText(R.string.follow);
                                isUserFollowedArtist = false;
                                Toast.makeText(ArtistActivity.this, getString(R.string.toast_unfollowed), Toast.LENGTH_SHORT).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse<Boolean>> call, Throwable t) {

                    }
                });
            }
        });

        btnShuffle = includeTopPlaylistOption.findViewById(R.id.btnTopPlaylistOptionShuffle);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle) {
                    isShuffle = false;
                    btnShuffle.setIconTint(getResources().getColorStateList(R.color.primary));
                } else {
                    isShuffle = true;
                    btnShuffle.setIconTint(getResources().getColorStateList(R.color.neutral3));
                }
            }
        });

        rvListAlbum = findViewById(R.id.rvListAlbum);

        includeListSong = findViewById(R.id.included_list_song);
        rvListSong = includeListSong.findViewById(R.id.rvListSong);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvListSong.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration = new BottomOffsetDecoration(getResources().getDimensionPixelSize(R.dimen.bottom_offset));


        setArtistInfo();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(this, songList, new SongAdapter.OnItemClickListener() {
            @Override
            public void onSongClick(int position) {
                exoPlayerQueue.clear();
                exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));
                exoPlayerQueue.setCurrentPosition(position);
                if (isShuffle) {
                    exoPlayerQueue.setShuffle(true);
                } else {
                    exoPlayerQueue.setShuffle(false);
                }
                Log.d("SongClicked", "onSongClick: " + songList.get(position).getName() + " " + songList.get(position).getIdSong());
                Intent intent = new Intent(getApplicationContext(), SongDetailActivity.class);
                startActivity(intent);
            }

            @Override
            public void onPlayPlaylistClick(List<Song> songList) {
                Log.d("phatall","đã vô đây");
                exoPlayerQueue.clear();
                exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));
                exoPlayerQueue.setCurrentPosition(0);
                if (isShuffle) {
                    exoPlayerQueue.setShuffle(true);
                } else {
                    exoPlayerQueue.setShuffle(false);
                }

                Intent intent = new Intent(getApplicationContext(), SongDetailActivity.class);
                startActivity(intent);
            }
        });
        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exoPlayerQueue.clear();
                exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(songList));
                exoPlayerQueue.setCurrentPosition(0);
                if (isShuffle) {
                    exoPlayerQueue.setShuffle(true);
                } else {
                    exoPlayerQueue.setShuffle(false);
                }

                Intent intent = new Intent(getApplicationContext(), SongDetailActivity.class);
                startActivity(intent);
            }
        });

        fetchSongs(apiService.getAllSongsByArtistId(artistId, page, 2));
        rvListSong.setAdapter(songAdapter);

        apiService.getSongLikedByIdUser(user.getId()).enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Long> likedSongIds = new ArrayList<>();
                    for (Song song : response.body().getData()) {
                        likedSongIds.add(song.getIdSong());
                    }
                    // Thêm log để kiểm tra
                    Log.d("ArtistActivity", "Liked Song IDs: " + likedSongIds);
                    // Gọi phương thức adapter để cập nhật hình ảnh trái tim
                    songAdapter.setLikedSongIds(likedSongIds);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
                Log.e("API_FAILURE", "Failed to fetch liked songs: " + t.getMessage());
            }
        });

        albumList = new ArrayList<>();
        albumAdapter = new AlbumAdapter(this, albumList, new AlbumAdapter.OnAlbumClickListener() {
            @Override
            public void onAlbumClick(Album album) {
                Log.d("ArtistActivity", "Album clicked: ID=" + album.getIdAlbum() + ", Name=" + album.getName());
                Intent intent = new Intent(getApplicationContext(), TopicActivity.class);
                intent.putExtra("albumId", String.valueOf(album.getIdAlbum()));
                intent.putExtra("topic", "1");
                startActivity(intent);
            }
        });
        RecyclerView.LayoutManager layoutManagerAlbum = new GridLayoutManager(getApplicationContext(), 2);
        rvListAlbum.setLayoutManager(layoutManagerAlbum);
        rvListAlbum.addItemDecoration(itemDecoration);
        rvListAlbum.setAdapter(albumAdapter);
        apiService.getAlbumsByArtistId(artistId).enqueue(new Callback<GenericResponse<List<Album>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Album>>> call, Response<GenericResponse<List<Album>>> response) {
                if (response.isSuccessful()) {
                    albumList.clear();
                    Log.d("Album List Size Before", "Before: " + albumList.size());
                    albumList.addAll(response.body().getData());
                    Log.d("Album List Size After", "After: " + albumList.size());
                    albumAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Album>>> call, Throwable t) {

            }
        });
    }

    private void setArtistInfo() {
        ArtistHelper.getArtistById(artistId, new ArtistHelper.ArtistCallback() {
            @Override
            public void onSuccess(Artist artist) {
                tvPlaylistTitle.setText(artist.getNickname());
                Glide.with(getApplicationContext()).load(artist.getAvatar()).into(coverPic);
                GradientHelper.applyGradient(getApplicationContext(), includeTopPlaylist, String.valueOf(artist.getAvatar()));
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

        ArtistHelper.getSongCountByArtistId(artistId, new ArtistHelper.SongCountCallback() {
            @Override
            public void onSuccess(int songCount) {
                tvPlaySongCount.setText(getString(R.string.label_songs, songCount));
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    private void fetchSongs(Call<GenericResponse<SongResponse>> call) {
        call.enqueue(new Callback<GenericResponse<SongResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<SongResponse>> call, Response<GenericResponse<SongResponse>> response) {
                if (response.isSuccessful()) {
                    List<Song> newList = response.body().getData().getContent();
                    totalPages = response.body().getData().getTotalPages();
                    songList.addAll(newList);
                    page++;
                    songAdapter.notifyDataSetChanged();

                    if (page >= totalPages) {
                        isLastPage = true;
                        btnLoadMore.setVisibility(View.GONE);
                    }
                }
                isLoading = false; // ✅ Chỉ tắt loading sau khi xử lý xong
            }

            @Override
            public void onFailure(Call<GenericResponse<SongResponse>> call, Throwable t) {
                Log.d("ArtistActivity", "onFailure: " + t.getMessage());
                isLoading = false; // ✅ Tắt luôn nếu lỗi xảy ra
            }
        });
    }

    private void loadNextPage() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page < totalPages) {
                    APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    fetchSongs(apiService.getAllSongsByArtistId(artistId, page, 2));
                } else {
                    isLoading = false;
                    isLastPage = true;
                    btnLoadMore.setVisibility(View.GONE);
                }
            }
        }, 500); // delay 500ms tạo cảm giác "loading"
    }


}