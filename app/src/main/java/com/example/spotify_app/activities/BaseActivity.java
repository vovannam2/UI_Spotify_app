package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.helpers.GradientHelper;
import com.example.spotify_app.helpers.ProgressBarUpdater;
import com.example.spotify_app.services.ExoBuilderService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.spotify_app.fragments.SongDetailFragment;

import java.util.concurrent.CountDownLatch;

public abstract class BaseActivity extends AppCompatActivity {

    protected ExoPlayer exoPlayer;
    private ExoBuilderService exoBuilderService;
    protected TextView tvSongTitle, tvSongArtist;
    protected ImageView ivSongImage;
    protected LinearProgressIndicator progressIndicator;
    protected ProgressBarUpdater progressBarUpdater;
    protected MaterialButton playPauseBtn;
    protected ExoPlayerQueue exoPlayerQueue;
    protected
    View includeMiniPlayer;
    View container;
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exoBuilderService = new ExoBuilderService(this, new ExoBuilderService.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                exoPlayer = exoBuilderService.getExoPlayer();
                exoPlayerQueue = ExoPlayerQueue.getInstance();
                progressBarUpdater = new ProgressBarUpdater(exoPlayer, progressIndicator);
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                        if (mediaItem != null) {
                            MediaMetadata metadata = mediaItem.mediaMetadata;
                            updateMiniplayer(metadata);
                            progressBarUpdater.startUpdating();
                            
                            // Cập nhật gradient cho SongDetailActivity nếu đang hiển thị
                            if (getClass().equals(SongDetailActivity.class)) {
                                View container = findViewById(R.id.container);
                                if (container != null && metadata.artworkUri != null) {
                                    GradientHelper.applyDoubleGradient(getApplicationContext(), container, String.valueOf(metadata.artworkUri));
                                }
                            }
                        }
                    }
                });

                if (exoPlayerQueue.getCurrentMediaItem() != null) {
                    MediaMetadata metadata = exoPlayerQueue.getCurrentMediaItem().mediaMetadata;
                    updateMiniplayer(metadata);
                    progressBarUpdater.startUpdating();
                }

                if (includeMiniPlayer != null) {
                    if (exoPlayer.getCurrentMediaItem() == null) {
                        includeMiniPlayer.setVisibility(View.GONE);
                    } else {
                        includeMiniPlayer.setVisibility(View.VISIBLE);
                    }
                }
                }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initMiniPlayer() {
        includeMiniPlayer = findViewById(R.id.miniplayer);

        includeMiniPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                includeMiniPlayer.setVisibility(View.GONE);
                showSongDetailFragment();
            }
        });

        tvSongTitle = includeMiniPlayer.findViewById(R.id.tvSongTitle);
        tvSongArtist = includeMiniPlayer.findViewById(R.id.tvSongArtist);
        ivSongImage = includeMiniPlayer.findViewById(R.id.imSongAvt);
        progressIndicator = includeMiniPlayer.findViewById(R.id.pbSongProgress);
        playPauseBtn = includeMiniPlayer.findViewById(R.id.btnSongOption);
        container = includeMiniPlayer.findViewById(R.id.conatiner);

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                    playPauseBtn.setIcon(getDrawable(R.drawable.ic_32dp_filled_play));
                } else {
                    exoPlayer.play();
                    playPauseBtn.setIcon(getDrawable(R.drawable.ic_32dp_filled_pause));
                }
            }
        });

        if (exoPlayer != null && exoPlayer.isPlaying()) {
            playPauseBtn.setIcon(getDrawable(R.drawable.ic_32dp_filled_pause));
        } else {
            playPauseBtn.setIcon(getDrawable(R.drawable.ic_32dp_filled_play));
        }
    }

    protected void  updateMiniplayer(MediaMetadata metadata) {
        if (metadata == null) {
            return;
        }

        Log.d("BaseActivity", "updateMiniplayer: " + metadata.title);
        tvSongTitle.setText(metadata.title);
        tvSongArtist.setText(metadata.extras.getString("artist"));
        Glide.with(getApplicationContext()).load(metadata.artworkUri).into(ivSongImage);
        GradientHelper.applyDoubleGradient(getApplicationContext(), container, String.valueOf(metadata.artworkUri));
        progressIndicator.setProgress((int) exoPlayer.getCurrentPosition() / 1000);
        if (playPauseBtn != null && exoPlayer != null) {
            if (exoPlayer.isPlaying()) {
                playPauseBtn.setIcon(getDrawable(R.drawable.ic_32dp_filled_pause));
            } else {
                playPauseBtn.setIcon(getDrawable(R.drawable.ic_32dp_filled_play));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (includeMiniPlayer != null && exoPlayer != null) {
            if (exoPlayer.getCurrentMediaItem() == null) {
                includeMiniPlayer.setVisibility(View.GONE);
            } else {
                includeMiniPlayer.setVisibility(View.VISIBLE);
                MediaItem currentSong = exoPlayer.getCurrentMediaItem();
                MediaMetadata metadata = currentSong.mediaMetadata;
                updateMiniplayer(metadata);
                progressBarUpdater.startUpdating();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (exoPlayer != null) {
            if (exoPlayer.getCurrentMediaItem() != null) {
                progressBarUpdater.stopUpdating();
            }
        }
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            if (exoPlayer.getCurrentMediaItem() != null) {
                progressBarUpdater.stopUpdating();
            }
        }
    }

    public void showSongDetailFragment() {
        Intent intent = new Intent(this, SongDetailActivity.class);
        startActivity(intent);
    }
}