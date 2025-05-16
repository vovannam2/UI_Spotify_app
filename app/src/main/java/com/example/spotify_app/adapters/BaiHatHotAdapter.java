package com.example.spotify_app.adapters;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.Check;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaiHatHotAdapter extends RecyclerView.Adapter<BaiHatHotAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> baiHatArrayList;
    private SharedPreferences prefs;
    private APIService apiService;
    private final OnItemClickListener onItemClickListener;

    public BaiHatHotAdapter(Context context, ArrayList<Song> baiHatArrayList, OnItemClickListener onItemClickListener) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.context = context;
        this.baiHatArrayList = baiHatArrayList;
        this.prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.onItemClickListener = onItemClickListener;
        this.apiService = RetrofitClient.getRetrofit().create(APIService.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bai_hat_hot_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song baiHat = baiHatArrayList.get(position);
        holder.position.setText(String.format("%02d", position + 1));
        
        holder.tvTenCaSi.setText(baiHat.getArtistName());
        holder.tvTenBaiHatHot.setText(baiHat.getName());
        Picasso.get().load(baiHat.getImage()).into(holder.imgHinhBaiHatHot);

        // Kiểm tra trạng thái yêu thích
        User user = SharePrefManagerUser.getInstance(context.getApplicationContext()).getUser();
        
        apiService.getSongLikedByIdUser(user.getId()).enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse<List<Song>> genericResponse = response.body();
                    if (genericResponse.isSuccess()) {
                        List<Song> likedSongs = genericResponse.getData();
                        boolean isLiked = false;
                        if (likedSongs != null && !likedSongs.isEmpty()) {
                            for (Song likedSong : likedSongs) {
                                if (baiHat.getIdSong().equals(likedSong.getIdSong())) {
                                    isLiked = true;
                                    break;
                                }
                            }
                        }
                        holder.imgLuotThich.setImageResource(isLiked ? R.drawable.ic_red_love : R.drawable.ic_white_love);
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
                Log.e("API_FAILURE", "Failed to fetch liked songs: " + t.getMessage());
            }
        });

        // Xử lý sự kiện click vào nút thích
        holder.imgLuotThich.setOnClickListener(v -> {
            apiService.toggleLike(baiHat.getIdSong(), (long)user.getId()).enqueue(new Callback<GenericResponse<Boolean>>() {
                @Override
                public void onResponse(Call<GenericResponse<Boolean>> call, Response<GenericResponse<Boolean>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean isLiked = response.body().getData();
                        holder.imgLuotThich.setImageResource(isLiked ? R.drawable.ic_red_love : R.drawable.ic_white_love);
                        Toast.makeText(context, isLiked ? "Đã thích bài hát" : "Đã bỏ thích bài hát", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("test", "Lỗi hoặc phản hồi không hợp lệ");
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<Boolean>> call, Throwable t) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return baiHatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenBaiHatHot, tvTenCaSi, position;
        ImageView imgHinhBaiHatHot, imgLuotThich;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBaiHatHot = itemView.findViewById(R.id.tvBaiHatHot);
            tvTenCaSi = itemView.findViewById(R.id.tvCaSiBaiHatHot);
            imgHinhBaiHatHot = itemView.findViewById(R.id.imageViewBaiHatHot);
            imgLuotThich = itemView.findViewById(R.id.imageViewLuotThich);
            position = itemView.findViewById(R.id.position);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onSongClick(position);
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
