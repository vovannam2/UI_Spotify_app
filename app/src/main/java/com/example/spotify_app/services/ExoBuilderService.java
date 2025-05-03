package com.example.spotify_app.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.media3.exoplayer.ExoPlayer;

public class ExoBuilderService {
    private Context context;
    private ExoService exoService;
    private ExoPlayer exoPlayer;
    private ExoPlayerQueue exoPlayerQueue;
    private ServiceReadyCallback serviceReadyCallback;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("ExoBuilder", "onServiceConnected: " + componentName);
            exoService = ((ExoService.MusicBinder) iBinder).getService();
            exoPlayer = exoService.getExoPlayer();
//            exoPlayerQueue = exoService.getExoPlayerQueue();
            if (serviceReadyCallback != null) {
                serviceReadyCallback.onServiceReady();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("ExoBuilder", "onServiceDisconnected: " + componentName);
        }
    };

    public ExoBuilderService(Context context, ServiceReadyCallback serviceReadyCallback) {
        this.context = context;
        this.serviceReadyCallback = serviceReadyCallback;
        bindService();
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
    public ExoPlayerQueue getExoPlayerQueue() {
        return exoPlayerQueue;
    }

    public void bindService() {
        context.startService(new Intent(context, ExoService.class));
        if (context.bindService(new Intent(context, ExoService.class), serviceConnection, Context.BIND_AUTO_CREATE)) {
            Log.d("ExoBuilder", "bindService: success");
        } else {
            Log.d("ExoBuilder", "bindService: failed");
        };
    }

    public void unbindService() {
        if (exoService != null) {
            context.unbindService(serviceConnection);
        }
    }
    public interface ServiceReadyCallback {
        void onServiceReady();
    }

}
