package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.media3.common.MediaMetadata;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.adapters.SongAdapter;
import com.example.spotify_app.decorations.BottomOffsetDecoration;
import com.example.spotify_app.fragments.SongDetailFragment;
import com.example.spotify_app.helpers.DialogHelper;
import com.example.spotify_app.helpers.GradientHelper;
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.listeners.DialogClickListener;
import com.example.spotify_app.listeners.PaginationScrollListener;
import com.example.spotify_app.models.Album;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Playlist;
import com.example.spotify_app.models.PlaylistResponse;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongResponse;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicActivity extends BaseActivity {
    private String topic;
    private List<Song> songList;
    private SongAdapter songAdapter;
    private int page, size, totalPages;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean isShuffle = false;
    private boolean isEditState = true;
    Handler handler;
    Runnable runnable;
    private ExoPlayerQueue exoPlayerQueue;
    private SongDetailFragment songDetailFragment;
    private APIService apiService;
    ImageView coverPic;
    EditText edtPlaylistTitle;
    TextView tvPlaylistIntro;
    TextView tvPlaylistSongCount;
    MaterialButton deletePlaylistButton;
    MaterialButton editPlaylistNameButton;
    RecyclerView rvListSong;
    View includeTopPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        initMiniPlayer();
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        topic = getIntent().getStringExtra("topic");

        includeTopPlaylist = findViewById(R.id.included_top_playlist);
        coverPic = includeTopPlaylist.findViewById(R.id.imCoverPicture);
        edtPlaylistTitle = includeTopPlaylist.findViewById(R.id.edtPlaylistTitle);
        tvPlaylistIntro = includeTopPlaylist.findViewById(R.id.tvPlaylistIntro);
        tvPlaylistSongCount = includeTopPlaylist.findViewById(R.id.tvPlaylistSongCount);
        editPlaylistNameButton = includeTopPlaylist.findViewById(R.id.btn_edit_name);
        tvPlaylistSongCount.setVisibility(View.GONE);
        editPlaylistNameButton.setVisibility(View.GONE);
        edtPlaylistTitle.setEnabled(false);

        View includeOption = findViewById(R.id.included_top_playlist_option);

        View includeListSong = findViewById(R.id.included_list_song);
        deletePlaylistButton = includeOption.findViewById(R.id.btn_delete_playlist);
        rvListSong = includeListSong.findViewById(R.id.rvListSong);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvListSong.setLayoutManager(layoutManager);
//        RecyclerView.ItemDecoration itemDecoration = new VerticalSpaceItemDecoration(R.dimen.spacing_4dp);
//        rvListSong.addItemDecoration(itemDecoration);
        rvListSong.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            public void loadMoreItems() {
                isLoading = true;
                loadNextPage(topic);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

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
        switch (topic) {
            case "trending":
                songList.clear();
                page = 0;
                size = 10;
                coverPic.setImageResource(R.drawable.trending_cover);
                GradientHelper.applyGradient(this, includeTopPlaylist, R.drawable.trending_cover);
                edtPlaylistTitle.setText(R.string.trending_title);
                tvPlaylistIntro.setText(R.string.trending_intro);
                deletePlaylistButton.setVisibility(View.GONE);

                fetchSongs(apiService.getMostViewSong(page, size));
                RecyclerView.ItemDecoration itemDecoration = new BottomOffsetDecoration(120);
                rvListSong.addItemDecoration(itemDecoration);
                rvListSong.setAdapter(songAdapter);
                break;
            case "favorite":
                songList.clear();
                page = 0;
                size = 10;
                coverPic.setImageResource(R.drawable.favorite_cover);
                GradientHelper.applyGradient(this, includeTopPlaylist, R.drawable.favorite_cover);
                edtPlaylistTitle.setText(R.string.favorite_title);
                tvPlaylistIntro.setText(R.string.favorite_intro);
                deletePlaylistButton.setVisibility(View.GONE);

                fetchSongs(apiService.getMostLikeSong(page, size));
                rvListSong.setAdapter(songAdapter);
                break;
            case "topArtist":
                coverPic.setImageResource(R.drawable.top_artist_cover);
                GradientHelper.applyGradient(this, includeTopPlaylist, R.drawable.top_artist_cover);
                edtPlaylistTitle.setText(R.string.topartist_title);
                tvPlaylistIntro.setText(R.string.topartist_intro);
                includeOption.setVisibility(View.GONE);
                break;
            case "newReleased":
                songList.clear();
                page = 0;
                size = 10;
                coverPic.setImageResource(R.drawable.new_released);
                GradientHelper.applyGradient(this, includeTopPlaylist, R.drawable.new_released);
                edtPlaylistTitle.setText(R.string.newreleased_title);
                tvPlaylistIntro.setText(R.string.newreleased_intro);
                deletePlaylistButton.setVisibility(View.GONE);
                fetchSongs(apiService.getSongNewReleased(page, size));
                rvListSong.setAdapter(songAdapter);
                break;

            default:
                tvPlaylistSongCount.setVisibility(View.VISIBLE);
                editPlaylistNameButton.setVisibility(View.VISIBLE);
                Log.d("TopicActivity", "onCreate: " + getIntent().getStringExtra("albumId"));
                if (getIntent().getStringExtra("albumId") != null) {
                    int albumId = Integer.parseInt(getIntent().getStringExtra("albumId"));
                    getIntent().removeExtra("albumId");
                    getPlaylistByAlbumId(albumId);
                }
                else {
                    getPlaylistById(Integer.parseInt(topic));
                    renamePlaylistIfNeeded(Integer.parseInt(topic));
                    deletePlaylistIfNeeded(Integer.parseInt(topic));
                }
                rvListSong.setAdapter(songAdapter);
                invalidateOptionsMenu();
                break;
        }

        MaterialButton btnShuffle = includeOption.findViewById(R.id.btnTopPlaylistOptionShuffle);
        MaterialButton btnPlayAll = includeOption.findViewById(R.id.btnTopPlaylistOptionPlay);

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShuffle = !isShuffle;
                if (isShuffle) {
                    btnShuffle.setIconTint(getResources().getColorStateList(R.color.primary));
                } else {
                    btnShuffle.setIconTint(getResources().getColorStateList(R.color.neutral3));
                }
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
    }

    private void getPlaylistByAlbumId(int albumId) {
        apiService.getAlbumById(albumId).enqueue(new Callback<GenericResponse<Album>>() {
            @Override
            public void onResponse(Call<GenericResponse<Album>> call, Response<GenericResponse<Album>> response) {
                if (response.isSuccessful()) {
                    Album album = response.body().getData();
                    if (album != null) {
                        loadAlbumTop(album);
                        loadAlbumSongs(album.getSongs());
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Album>> call, Throwable t) {
            }
        });
    }

    private void fetchSongs(Call<GenericResponse<SongResponse>> call) {
        call.enqueue(new Callback<GenericResponse<SongResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<SongResponse>> call, Response<GenericResponse<SongResponse>> response) {
                if (response.isSuccessful()) {
                    List<Song> newList =  response.body().getData().getContent();
                    totalPages = response.body().getData().getTotalPages();
                    songList.addAll(newList);
                    page++;
                    songAdapter.notifyDataSetChanged();
                    Log.d("TopicFragment", "Total pages: " + response.body().getData().getTotalPages());
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<SongResponse>> call, Throwable t) {
                Log.d("TopicFragment", "onFailure: " + t.getMessage());
            }
        });
    }

    private void loadNextPage(String topic) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (page < totalPages) {
                    APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    Call<GenericResponse<SongResponse>> call = null;
                    switch (topic) {
                        case "trending":
                            call = apiService.getMostViewSong(page, size);
                            break;
                        case "favorite":
                            call = apiService.getMostLikeSong(page, size);
                            break;
                        case "newReleased":
                            call = apiService.getSongNewReleased(page, size);
                            break;
                    }
                    fetchSongs(call);
                }
                isLoading = false;
                if (page == totalPages) {
                    isLastPage = true;
                }
            }
        }, 500);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void updateMiniplayer(MediaMetadata metadata) {
        super.updateMiniplayer(metadata);
        Log.d("TopicActivity", "updateMiniplayer: updated");
    }

    private void loadTopPlaylist(Playlist playlist) {
        tvPlaylistIntro.setVisibility(View.GONE);
        Glide.with(TopicActivity.this)
                .load(playlist.getImage())
                .into(coverPic);
        edtPlaylistTitle.setText(playlist.getName());
        tvPlaylistSongCount.setText(getString(R.string.label_songs, playlist.getSongCount()));

        GradientHelper.applyGradient(this, includeTopPlaylist, playlist.getImage());
    }

    private void loadAlbumTop(Album album) {
        tvPlaylistIntro.setVisibility(View.GONE);
        Glide.with(TopicActivity.this)
                .load(album.getImage())
                .into(coverPic);
        edtPlaylistTitle.setText(album.getName());
        tvPlaylistSongCount.setText(getString(R.string.label_songs, album.getSongs().size()));

        GradientHelper.applyGradient(this, includeTopPlaylist, album.getImage());
    }

    private void loadAlbumSongs(List<Song> songs) {
        songList.addAll(songs);
        songAdapter.notifyDataSetChanged();
    }


    private void loadPlaylistSongs(List<Song> songs) {
        songList.addAll(songs);
        rvListSong.setAdapter(songAdapter);
        songAdapter.notifyDataSetChanged();
    }


    private void getPlaylistById(int idPlaylist) {
        apiService.getPlaylistById(Integer.parseInt(topic)).enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                if (response.isSuccessful()) {
                    Playlist playlist = response.body().getData();
                    if (playlist != null) {
                        loadTopPlaylist(playlist);
                        loadPlaylistSongs(playlist.getSongs());
                    }
                }
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {

            }
        });
    }

    private void renamePlaylistIfNeeded(int i) {
        editPlaylistNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPlaylistTitle.setEnabled(true);
                edtPlaylistTitle.requestFocus();
                edtPlaylistTitle.setSelection(edtPlaylistTitle.getText().length());
                handler = new Handler();
                if (isEditState) {
                    editPlaylistNameButton.setIcon(getDrawable(R.drawable.ic_24dp_filled_check_circle));
                    edtPlaylistTitle.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (runnable != null) {
                                handler.removeCallbacks(runnable);
                            }
                            runnable = new Runnable() {
                            @Override
                            public void run() {
                                String newName = edtPlaylistTitle.getText().toString();
                                if (newName.isEmpty()) {
                                    editPlaylistNameButton.setEnabled(false);
                                    /*edtPlaylistTitle.setError(getText(R.string.error_required_field));*/
                                    tvPlaylistIntro.setVisibility(View.VISIBLE);
                                    tvPlaylistIntro.setText(getText(R.string.error_required_field));
                                    tvPlaylistIntro.setTextColor(getColor(R.color.accent_error));
                                } else {
                                    apiService.isPlaylistNameExists(newName).enqueue(new Callback<ResponseMessage>() {
                                        @Override
                                        public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body().getData().equals(Boolean.TRUE)) {
                                                    editPlaylistNameButton.setEnabled(false);
                                                    tvPlaylistIntro.setVisibility(View.VISIBLE);
                                                    tvPlaylistIntro.setText(getText(R.string.error_playlist_name_exists));
                                                    tvPlaylistIntro.setTextColor(getColor(R.color.accent_error));
                                                } else {
                                                    editPlaylistNameButton.setEnabled(true);
                                                    tvPlaylistIntro.setVisibility(View.GONE);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseMessage> call, Throwable t) {}
                                    });
                                }
                            }
                        };
                        handler.postDelayed(runnable, 1000);
                        }
                    });
                } else {
                    String newName = edtPlaylistTitle.getText().toString();

                    apiService.updatePlaylistName(i, newName).enqueue(new Callback<ResponseMessage>() {
                        @Override
                        public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getData().equals(Boolean.TRUE)) {
                                    edtPlaylistTitle.setEnabled(false);
                                    tvPlaylistIntro.setVisibility(View.GONE);
                                    Toast.makeText(TopicActivity.this, getText(R.string.toast_updated_playlist_name), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseMessage> call, Throwable t) {
                        }
                    });
                    editPlaylistNameButton.setIcon(getDrawable(R.drawable.ic_24dp_outline_edit));
                }
                isEditState = !isEditState;
            }
        });
    }

    private void deletePlaylistIfNeeded(int idPlaylist) {
        deletePlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper dialog = new DialogHelper(view.getContext(), new DialogClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        apiService.deletePlaylist(Integer.parseInt(topic)).enqueue(new Callback<ResponseMessage>() {
                            @Override
                            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(TopicActivity.this, getText(R.string.toast_deleted_playlist), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(TopicActivity.this, MainActivity.class);
                                    intent.putExtra("fragmentId", R.id.fragment_library);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                            }
                        });
                    }

                    @Override
                    public void onNegativeButtonClick() { }
                });
                dialog.show();
                dialog.setTitle(getString(R.string.dialog_title_confirm_delete));
                dialog.setMessage(getString(R.string.dialog_message_confirm_delete));
                dialog.setPositiveButtonContent(getString(R.string.button_delete));
                dialog.setNegativeButtonContent(getString(R.string.button_cancel));
                dialog.setPositiveButtonColor(R.color.error);

            }
        });
    }
}