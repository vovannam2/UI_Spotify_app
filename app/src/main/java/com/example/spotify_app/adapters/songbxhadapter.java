package com.example.spotify_app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.fragments.BottomSheetDialog;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class songbxhadapter extends RecyclerView.Adapter<songbxhadapter.SongViewHolder> implements BottomSheetDialog.OnSongActionListener {
    private final Context context;
    private List<Song> songList;

    private List<Long> likedSongIds;

    private final OnItemClickListener onItemClickListener;

    public songbxhadapter(Context context, List<Song> songList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.songList = songList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }
    public void setLikedSongIds(List<Long> likedSongIds) {
        this.likedSongIds = likedSongIds;
        notifyDataSetChanged();
    }
    public void toggleLikeStatus(Long songId) {
        if (likedSongIds == null) {
            likedSongIds = new ArrayList<>();
        }

        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getIdSong().equals(songId)) {
                if (likedSongIds.contains(songId)) {
                    likedSongIds.remove(songId);
                } else {
                    likedSongIds.add(songId);
                }
                notifyItemChanged(i);
                break;
            }
        }
    }
    public void onSongLikedStatusChanged(Long songId, boolean isLiked) {
        if (likedSongIds == null) {
            likedSongIds = new ArrayList<>();
        }

        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getIdSong().equals(songId)) {
                if (isLiked) {
                    if (!likedSongIds.contains(songId)) {
                        likedSongIds.add(songId);
                    }
                } else {
                    likedSongIds.remove(songId);
                }
                notifyItemChanged(i);
                break;
            }
        }
    }



    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_songbxh, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song == null) return;
        
        // Set số thứ tự
        holder.position.setText(String.valueOf(position + 1));
        
        holder.tvSongTitle.setText(song.getName());
        Glide.with(context).load(song.getImage())
                .transform(new RoundedCornersTransformation(10, 0))
                .into(holder.imSongAvt);

        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getArtistsBySongId(song.getIdSong()).enqueue(new Callback<GenericResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Artist>>> call, Response<GenericResponse<List<Artist>>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    Artist artist = response.body().getData().get(0);
                    song.setArtistName(artist.getNickname());
                    holder.tvSongArtist.setText(artist.getNickname());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Artist>>> call, Throwable t) {
                holder.tvSongArtist.setText("");
                Log.d("SongAdapter", "onFailure: " + t.getMessage());
            }

        });
        holder.songActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.setContent(song.getIdSong(), song.getImage(), song.getName(), song.getArtistName());
                bottomSheetDialog.setOnSongActionListener(songbxhadapter.this);
                bottomSheetDialog.show(((androidx.fragment.app.FragmentActivity) context)
                        .getSupportFragmentManager(), "ModalBottomSheet");
            }
        });
        // Kiểm tra trạng thái yêu thích
        if (likedSongIds != null && likedSongIds.contains(song.getIdSong())) {
            holder.imgLuotThich.setImageResource(R.drawable.ic_red_love);
        } else {
            holder.imgLuotThich.setImageResource(R.drawable.ic_white_love);
        }

    }

    @Override
    public int getItemCount() {
        return songList == null ? 0 : songList.size();
    }

    @Override
    public void onSongDeleted(Long songId) {
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getIdSong().equals(songId)) {
                songList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
        int songs = getItemCount();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imSongAvt, imgLuotThich;
        TextView tvSongTitle, tvSongArtist, position;
        MaterialButton songActionButton;
        
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imSongAvt = itemView.findViewById(R.id.imSongAvt);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            songActionButton = itemView.findViewById(R.id.btnSongOption);
            imgLuotThich = itemView.findViewById(R.id.imageViewLuotThich);
            position = itemView.findViewById(R.id.position);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getBindingAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onSongClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onSongClick(int position);
        void onPlayPlaylistClick(List<Song> songList);
    }
}
