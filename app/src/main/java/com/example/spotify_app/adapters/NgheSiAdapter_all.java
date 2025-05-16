package com.example.spotify_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.models.Artist;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class NgheSiAdapter_all extends RecyclerView.Adapter<NgheSiAdapter_all.MyViewHolder> {
    private final Context context;
    private List<Artist> artistList;
    private final OnItemClickListener onItemClickListener;

    public NgheSiAdapter_all(Context context, List<Artist> artistList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.artistList = artistList;
        this.onItemClickListener = onItemClickListener;
    }

    // Sửa lại phương thức setArtistList để tạo lại danh sách artist
    public void setArtistList(List<Artist> artistList) {
        this.artistList = new ArrayList<>(artistList); // Tạo danh sách mới thay vì clear và addAll
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_all, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.tenNgheSi.setText(artist.getNickname());

        Glide.with(context)
                .load(artist.getAvatar())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView image; // Dùng ShapeableImageView thay vì CircleImageView
        public TextView tenNgheSi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.artist_img); // Đảm bảo ID đúng trong layout
            tenNgheSi = itemView.findViewById(R.id.artist_name); // Đảm bảo ID đúng trong layout
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) { // Kiểm tra nếu vị trí hợp lệ
                    Artist artist = artistList.get(position);
                    onItemClickListener.onItemClick(artist);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Artist artist);
    }
}
