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
import com.example.spotify_app.models.Song;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class SongAddToLibraryAdapter extends RecyclerView.Adapter<SongAddToLibraryAdapter.SongAddToLibraryViewHolder> {
    private final Context context;
    private final List<Song> songs;

    private final List<Long> checkedSongIds;

    private final SparseBooleanArray checkedSongs;

    public SongAddToLibraryAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
        this.checkedSongIds = new ArrayList<>();
        this.checkedSongs = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public SongAddToLibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_song_library, parent, false);
        return new SongAddToLibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAddToLibraryViewHolder holder, int position) {
        Song song = songs.get(position);
        Glide.with(context)
                .load(song.getImage())
                .into(holder.songImage);
        holder.songTitle.setText(song.getName());
        holder.artistName.setText(song.getArtistName());

        // Remove previous listeners
        holder.checkBox.setOnCheckedChangeListener(null);
        // Set the checkbox state based on the SparseBooleanArray
        holder.checkBox.setChecked(checkedSongs.get(position, false));
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    // Update the SparseBooleanArray
                    checkedSongs.put(position, isChecked);
                    Long songId = song.getIdSong();
                    if (isChecked) {
                        checkedSongIds.add(songId);
                    } else {
                        checkedSongIds.remove(songId);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs == null ? 0 : songs.size();
    }

    public List<Long> getCheckedSongIds() {
        return checkedSongIds;
    }

    public void clearAllCheckedSongs() {
        checkedSongs.clear();
        notifyDataSetChanged();
    }

    public static class SongAddToLibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView songImage;
        TextView songTitle;
        TextView artistName;
        MaterialCheckBox checkBox;
        public SongAddToLibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.imSongAvt);
            songTitle = itemView.findViewById(R.id.tvSongTitle);
            artistName = itemView.findViewById(R.id.tvSongArtist);
            checkBox = itemView.findViewById(R.id.cb_add_song);
        }
    }

}
