//SearchMusicAdapter
package com.example.spotify_app.adapters;

import android.content.Context;
import android.content.Intent;
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

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> baiHatArrayList;
    private SharedPreferences prefs;
    APIService apiService ;

    public SearchMusicAdapter(Context context, ArrayList<Song> baiHatArrayList) {
        this.context = context;
        this.baiHatArrayList = baiHatArrayList;
        this.prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_music_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song baiHat = baiHatArrayList.get(position);
        holder.tvTenBaiHatHot.setText(baiHat.getName());
        holder.tvTenCaSi.setText(baiHat.getArtistName());
        Picasso.get().load(baiHat.getImage()).into(holder.imgHinhBaiHatHot);
    }

    @Override
    public int getItemCount() {
        return baiHatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenBaiHatHot, tvTenCaSi;
        ImageView imgHinhBaiHatHot, imgLuotThich;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBaiHatHot = itemView.findViewById(R.id.tvSearchMusic);
            tvTenCaSi = itemView.findViewById(R.id.tvSearchMusicSinger);
            imgHinhBaiHatHot = itemView.findViewById(R.id.imageViewSearchMusic);
            imgLuotThich = itemView.findViewById(R.id.imageViewLuotThichSearchMusic);


            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public  void onClick(View v){
                    /*Intent intent = new Intent(context, PlayMusicActivity.class);
                    intent.putExtra("cakhuc", baiHatArrayList.get(getPosition()));
                    context.startActivity(intent);*/
                }
            });
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
                            Song currentSong = baiHatArrayList.get(getBindingAdapterPosition()); // Lấy bài hát hiện tại theo vị trí

                            // Kiểm tra xem bài hát hiện tại có nằm trong danh sách yêu thích không
                            for (Song likedSong : likedSongs) {
                                if (currentSong.getIdSong().equals(likedSong.getIdSong())) {
                                    imgLuotThich.setImageResource(R.drawable.ic_red_love); // Đổi biểu tượng trái tim thành màu đỏ
                                    break; // Thoát khỏi vòng lặp nếu tìm thấy
                                }
                            }
                        } else {
                            Log.e("API_ERROR", "Failed to fetch liked songs: " + genericResponse.getMessage());
                        }
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

                    apiService.toggleLike(currentSong.getIdSong(), (long)idUser).enqueue(new Callback<GenericResponse<Boolean>>() {
                        @Override
                        public void onResponse(Call<GenericResponse<Boolean>> call, Response<GenericResponse<Boolean>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                boolean isLiked = response.body().getData();
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
                        public void onFailure(Call<GenericResponse<Boolean>> call, Throwable t) {
                            Toast.makeText(context, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }
}
