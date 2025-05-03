package com.example.spotify_app.adapters;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.example.spotify_app.models.Check;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
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

    private  APIService apiService;

    private final OnItemClickListener onItemClickListener;

    public BaiHatHotAdapter(Context context, ArrayList<Song> baiHatArrayList ,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.baiHatArrayList = baiHatArrayList;
        this.prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        this.onItemClickListener = onItemClickListener;
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
        holder.tvTenCaSi.setText(baiHat.getArtistName());
        holder.tvTenBaiHatHot.setText(baiHat.getName());
        Picasso.get().load(baiHat.getImage()).into(holder.imgHinhBaiHatHot);
    }


    @Override
    public int getItemCount() {
        return baiHatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenBaiHatHot, tvTenCaSi;
        ImageView imgHinhBaiHatHot, imgLuotThich;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBaiHatHot = itemView.findViewById(R.id.tvBaiHatHot);
            tvTenCaSi = itemView.findViewById(R.id.tvCaSiBaiHatHot);
            imgHinhBaiHatHot = itemView.findViewById(R.id.imageViewBaiHatHot);
            imgLuotThich = itemView.findViewById(R.id.imageViewLuotThich);

            apiService = RetrofitClient.getRetrofit().create(APIService.class);
            final String idUserString = prefs.getString("idUser", "0");
            final int idUser = Integer.parseInt(idUserString);
            Call<GenericResponse<List<Song>>> callback = apiService.getSongLikedByIdUser(2);
            callback.enqueue(new Callback<GenericResponse<List<Song>>>() {
                @Override
                public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GenericResponse<List<Song>> genericResponse = response.body();
                        if (genericResponse.isSuccess()) { // Kiểm tra trạng thái phản hồi
                            List<Song> likedSongs = genericResponse.getData(); // Lấy danh sách bài hát yêu thích

                            // Kiểm tra vị trí hợp lệ
                            int position = getBindingAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) { // Kiểm tra vị trí hợp lệ
                                Song currentSong = baiHatArrayList.get(position); // Lấy bài hát hiện tại theo vị trí

                                // Kiểm tra xem bài hát hiện tại có nằm trong danh sách yêu thích không
                                boolean isLiked = false; // Biến flag để kiểm tra bài hát có được yêu thích không
                                if (likedSongs != null && !likedSongs.isEmpty()) { // Kiểm tra danh sách yêu thích không rỗng
                                    for (Song likedSong : likedSongs) {
                                        if (currentSong.getIdSong().equals(likedSong.getIdSong())) {
                                            isLiked = true;
                                            break; // Thoát khỏi vòng lặp nếu tìm thấy
                                        }
                                    }
                                }

                                // Cập nhật biểu tượng trái tim theo trạng thái yêu thích
                                if (isLiked) {
                                    imgLuotThich.setImageResource(R.drawable.ic_red_love); // Đổi biểu tượng trái tim thành màu đỏ
                                } else {
                                    imgLuotThich.setImageResource(R.drawable.ic_white_love); // Trái tim trắng nếu không yêu thích
                                }
                            } else {
                                Log.e("BaiHatHotAdapter", "Invalid position: " + position);
                            }
                        } else {
                            Log.e("API_ERROR", "Failed to fetch liked songs: " + genericResponse.getMessage());
                        }
                    } else {
                        Log.e("API_ERROR", "Response is not successful or body is null");
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
                    if (t instanceof java.net.UnknownHostException) {
                        Toast.makeText(context, "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Đã xảy ra lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("API_FAILURE", "Failed to fetch liked songs: " + t.getMessage());
                }
            });

            imgLuotThich.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition(); // dùng để thay thế getPosition() bị deprecated
                    if (position == RecyclerView.NO_POSITION) return; // tránh lỗi ngoài phạm vi

                    Song currentSong = baiHatArrayList.get(position);

                    apiService.toggleLike(currentSong.getIdSong(), (long)idUser).enqueue(new Callback<GenericResponse<Check>>() {
                        @Override
                        public void onResponse(Call<GenericResponse<Check>> call, Response<GenericResponse<Check>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                boolean isLiked = response.body().getData().isData();
                                if (isLiked) {
                                    imgLuotThich.setImageResource(R.drawable.ic_red_love);
                                    Toast.makeText(context, "Đã thích bài hát", Toast.LENGTH_SHORT).show();
                                } else {
                                    imgLuotThich.setImageResource(R.drawable.ic_white_love);
                                    Toast.makeText(context, "Đã bỏ thích bài hát", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse<Check>> call, Throwable t) {
                            Toast.makeText(context, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

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
