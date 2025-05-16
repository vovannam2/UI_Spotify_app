package com.example.spotify_app.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.models.Playlist;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAddToLibraryAdapter extends RecyclerView.Adapter<PlaylistAddToLibraryAdapter.SongAddToLibraryViewHolder> {
    private final Context context;
    private final List<Playlist> playlists;
    private final List<Long> checkedPlaylistIds;
    private final SparseBooleanArray checkedPlaylists;

    public PlaylistAddToLibraryAdapter(Context context, List<Playlist> playlists) {
        this.context = context;
        this.playlists = playlists;
        this.checkedPlaylistIds = new ArrayList<>();
        this.checkedPlaylists = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public SongAddToLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_to_playlist, parent, false);
        return new SongAddToLibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAddToLibraryViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        Glide.with(context)
                .load(playlist.getImage())
                .into(holder.playlistImage);
        holder.playlistTitle.setText(playlist.getName());
        holder.playlistSongCount.setText(context.getString(R.string.label_songs, playlist.getSongCount()));
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(checkedPlaylists.get(position, false));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //TODO: add chosen song to playlist
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    checkedPlaylists.put(position, isChecked);
                    Long id = (long) playlist.getIdPlaylist();
                    if (isChecked) {
                        checkedPlaylistIds.add(id);
                    } else {
                        checkedPlaylistIds.remove(id);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists == null ? 0 : playlists.size();
    }

    public List<Long> getCheckedPlaylistIds() {
        return checkedPlaylistIds;
    }

    public void clearAllCheckedPlaylists() {
        checkedPlaylists.clear();
        notifyDataSetChanged();
    }
    public static class SongAddToLibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView playlistImage;
        TextView playlistTitle;
        TextView playlistSongCount;
        MaterialCheckBox checkBox;
        public SongAddToLibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImage = itemView.findViewById(R.id.imv_playlist_image);
            playlistTitle = itemView.findViewById(R.id.tv_playlist_title);
            playlistSongCount = itemView.findViewById(R.id.tv_playlist_song_count);
            checkBox = itemView.findViewById(R.id.cb_add_playlist);
        }
    }

}
