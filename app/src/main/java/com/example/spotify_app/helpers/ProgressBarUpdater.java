package com.example.spotify_app.helpers;

import android.os.Handler;

import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ProgressBarUpdater {
    private ExoPlayer exoPlayer;
    private LinearProgressIndicator progressIndicator;
    private Handler handler = new Handler();

    private Runnable updateProgressBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null && progressIndicator != null) {
                progressIndicator.setProgress((int) (exoPlayer.getCurrentPosition() * 100 / exoPlayer.getDuration()));
                handler.postDelayed(this, 1000);
            }
        }
    };


    private Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying) {
                startUpdating();
            } else {
                stopUpdating();
            }
        }
    };

    public ProgressBarUpdater(ExoPlayer exoPlayer, LinearProgressIndicator progressIndicator) {
        this.exoPlayer = exoPlayer;
        this.progressIndicator = progressIndicator;
        exoPlayer.addListener(playerListener);
    }

    public void startUpdating() {
        handler.post(updateProgressBarRunnable);
    }

    public void stopUpdating() {
        handler.removeCallbacks(updateProgressBarRunnable);
    }

}
