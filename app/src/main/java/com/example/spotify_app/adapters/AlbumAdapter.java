package com.example.spotify_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.TopicActivity;
import com.example.spotify_app.models.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    Context context;
    private List<Album> albumList;

    private OnAlbumClickListener listener;


    public AlbumAdapter(Context context,  List<Album> albumList,OnAlbumClickListener onAlbumClickListener) {
        this.context = context;
        this.albumList = albumList;
        this.listener = onAlbumClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.tvTenAlbum.setText(album.getName());
        holder.tvCaSiAlbum.setText(album.getArtistName());
        Picasso.get().load(album.getImage()).into(holder.imgHinhAlbum);
        
        Log.d("AlbumAdapter", "Binding album at position " + position + ": ID=" + album.getIdAlbum() + ", Name=" + album.getName());
        
        holder.itemView.setOnClickListener(v -> {
            Log.d("AlbumAdapter", "Clicked album at position " + position + ": ID=" + album.getIdAlbum() + ", Name=" + album.getName());
            if (listener != null) {
                listener.onAlbumClick(album);
            } else {
                Log.e("AlbumAdapter", "Listener is null!");
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imgHinhAlbum;
        TextView tvTenAlbum, tvCaSiAlbum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHinhAlbum = itemView.findViewById(R.id.imageviewalbum);
            tvTenAlbum = itemView.findViewById(R.id.tvTenAlbum);
            tvCaSiAlbum = itemView.findViewById(R.id.tvTenCaSiAlbum);
        }
    }
    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

}
