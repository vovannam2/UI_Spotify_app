package com.example.spotify_app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaStyleNotificationHelper;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;

public class ExoService extends Service {

    private ExoPlayer exoPlayer;
    private ExoPlayerQueue exoPlayerQueue;
    //    private ExoPlayerQueue exoPlayerQueue;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "com.example.music_app.services.ExoService";
    private static final String CHANNEL_NAME = "ExoService";

    private final IBinder iBinder = new MusicBinder();
    private MediaSession mediaSession;
    private SongViewManager songViewManager;
    @Override
    public void onCreate() {
        super.onCreate();
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        exoPlayer = new ExoPlayer.Builder(this).build();
        songViewManager = SongViewManager.getInstance();
        //mediaSession = new MediaSession.Builder(this, exoPlayer).build();
        mediaSession = new MediaSession.Builder(this, exoPlayer)
            .setId("exo_player_session") // hoặc bất kỳ chuỗi nào bạn muốn, miễn là không rỗng và duy nhất
            .build();
        createNotificationChannel();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && exoPlayer.getPlayWhenReady()) {
                    songViewManager.startCount(exoPlayer.getCurrentMediaItem());
                    updateNotification();

                } else if (playbackState == Player.STATE_IDLE) {
                    songViewManager.stopCount();
                    stopForeground(true);
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if (mediaItem != null) {
                    exoPlayerQueue.setCurrentPosition(mediaItem.mediaMetadata.extras.getInt("position"));
                    updateNotification();
                }
            }

        });



    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public class MusicBinder extends Binder {
        public ExoService getService() {
            return ExoService.this;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private Notification createNotification() {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        MediaItem currentMediaItem = exoPlayerQueue.getCurrentMediaItem();
        String title;
        String artist;
        MediaMetadata metadata = null;
        if (currentMediaItem != null && currentMediaItem.mediaMetadata != null) {
            metadata = currentMediaItem.mediaMetadata;
            title = metadata.title.toString();
            artist = metadata.extras.getString("artist").toString();
        } else {
            title = "Unknown";
            artist = "Unknown";
        }

        Bitmap artworkBitmap = null;
        if (metadata.artworkUri != null) {
            try {
                artworkBitmap = Glide.with(this).asBitmap().load(metadata.artworkUri).submit().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d("Notification", "createNotification: Artist Name " + artist + " Title " + title);

        builder.setContentTitle(title)
                .setContentText(artist)
                .setSmallIcon(R.drawable.ic_32dp_outline_graphic_eq)
                .setLargeIcon(artworkBitmap)
                .setPriority(Notification.PRIORITY_LOW)
                .setOngoing(true)
                .setShowWhen(false)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_32dp_filled_skip_previous, "Previous", getPendingIntent("PREVIOUS"))
                .addAction(R.drawable.ic_32dp_filled_pause, "Pause", getPendingIntent("PAUSE"))
                .addAction(R.drawable.ic_32dp_filled_skip_next, "Next", getPendingIntent("NEXT"))
                .setStyle(new MediaStyleNotificationHelper.MediaStyle(mediaSession)
                        .setShowActionsInCompactView(0, 1, 2));

        return builder.build();
    }

    private PendingIntent getPendingIntent(String action) {
        Intent intent = new Intent(this, ExoService.class);
        intent.setAction(action);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void updateNotification() {
        Notification notification = createNotification();
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}