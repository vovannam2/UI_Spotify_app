package com.example.spotify_app.helpers;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;

import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.Song;

import java.util.ArrayList;
import java.util.List;

public class SongToMediaItemHelper {
    public static List<MediaItem> convertToMediaItem(List<Song> songList) {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song : songList) {
            Bundle extras = new Bundle();
            ArtistHelper.getArtistBySongId(song.getIdSong(), new ArtistHelper.ArtistCallback() {
                @Override
                public void onSuccess(Artist artist) {
                    extras.putString("artist", artist.getNickname());
                    Log.d("ConvertSong", "convertToMediaItem: " + song.getName() + " " + artist.getNickname());
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.d("SongDetailFragment", "onFailure: " + t.getMessage());
                }
            });
            extras.putLong("id", song.getIdSong());
            extras.putInt("position", songList.indexOf(song));
            MediaMetadata metadata = new MediaMetadata.Builder()
                    .setTitle(song.getName())
                    .setExtras(extras)
                    .setArtworkUri(Uri.parse(song.getImage()))
                    .build();
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.parse(song.getResource()))
                    .setMediaMetadata(metadata)
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;

    }
}
