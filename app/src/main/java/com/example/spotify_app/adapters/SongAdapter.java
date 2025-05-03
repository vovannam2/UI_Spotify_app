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

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements BottomSheetDialog.OnSongDeletedListener{
    private final Context context;
    private List<Song> songList;

    private final OnItemClickListener onItemClickListener;

    public SongAdapter(Context context, List<Song> songList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.songList = songList;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<Song> songList) {
        this.songList = songList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        if (song == null) return;
        holder.tvSongTitle.setText(song.getName());
        Glide.with(context).load(song.getImage())
                .transform(new RoundedCornersTransformation(10, 0))
                .into(holder.imSongAvt);
        holder.songActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                bottomSheetDialog.setContent(song.getIdSong(), song.getImage(), song.getName(), song.getArtistName());
                bottomSheetDialog.setOnSongDeletedListener(SongAdapter.this);
                bottomSheetDialog.show(((androidx.fragment.app.FragmentActivity) context)
                        .getSupportFragmentManager(), "ModalBottomSheet");
            }
        });
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getArtistsBySongId(song.getIdSong()).enqueue(new Callback<GenericResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Artist>>> call, Response<GenericResponse<List<Artist>>> response) {
                if (response.body() != null && response.body().getData() != null) {
                    Artist artist = response.body().getData().get(0);
                    holder.tvSongArtist.setText(artist.getNickname());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Artist>>> call, Throwable t) {
                holder.tvSongArtist.setText("");
                Log.d("SongAdapter", "onFailure: " + t.getMessage());
            }

        });
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
        ImageView imSongAvt;
        TextView tvSongTitle, tvSongArtist;
        MaterialButton songActionButton;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            imSongAvt = itemView.findViewById(R.id.imSongAvt);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvSongArtist = itemView.findViewById(R.id.tvSongArtist);
            songActionButton = itemView.findViewById(R.id.btnSongOption);

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
