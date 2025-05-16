package com.example.spotify_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.example.spotify_app.R;
import com.example.spotify_app.activities.ArtistActivity;
import com.example.spotify_app.activities.SongDetailActivity;
import com.example.spotify_app.activities.TopicActivity;
import com.example.spotify_app.helpers.SongToMediaItemHelper;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.QuangCao;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerAdapter extends PagerAdapter {
    Context context;
    ArrayList<QuangCao> arrayListBanner;

    private ExoPlayerQueue exoPlayerQueue;

    public BannerAdapter(Context context, ArrayList<QuangCao> arrayListBanner) {
        this.context = context;
        this.arrayListBanner = arrayListBanner;
    }

    @Override
    public int getCount() {
        return arrayListBanner.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.banner_item, null);

        ImageView imageBackgroundBanner = view.findViewById(R.id.imageViewBackgroundBanner);
        ImageView imgSongBanner = view.findViewById(R.id.imageViewBanner);
        TextView tvTitleSongBanner = view.findViewById(R.id.tvTitleBannerBaiHat);
        TextView tvNoiDung = view.findViewById(R.id.tvNoiDung);

        Picasso.get().load(arrayListBanner.get(position).getHinhAnh()).into(imageBackgroundBanner);
        QuangCao quangCao = arrayListBanner.get(position);
        // Load ảnh và tiêu đề dựa vào loại quảng cáo
        switch(quangCao.getLoaiQuangCao()) {
            case "song":
                // Hiển thị thông tin bài hát
                Picasso.get().load(quangCao.getHinhBaiHat()).into(imgSongBanner);
                tvTitleSongBanner.setText(quangCao.getTenBaiHat());
                break;

            case "album":
                // Hiển thị thông tin album
                Picasso.get().load(quangCao.getHinhAlbum()).into(imgSongBanner);
                tvTitleSongBanner.setText(quangCao.getTenAlbum());
                break;

            case "artist":
                // Hiển thị thông tin nghệ sĩ
                Picasso.get().load(quangCao.getHinhNgheSi()).into(imgSongBanner);
                tvTitleSongBanner.setText(quangCao.getTenNgheSi());
                break;
        }



        tvNoiDung.setText(arrayListBanner.get(position).getNoiDung());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuangCao quangCao = arrayListBanner.get(position);
                switch(quangCao.getLoaiQuangCao()) {
                    case "song":
                        // Gọi hàm playSongById với id của bài hát

                        playSongById(Long.valueOf(quangCao.getIdBaiHat()));
                        // Chuyển đến trang chi tiết bài hát
                        Intent songIntent = new Intent(context, SongDetailActivity.class);
                        context.startActivity(songIntent);


                        break;

                    case "album":
                        // Chuyển đến trang chi tiết album

                        Intent intent = new Intent(context, TopicActivity.class);
                        intent.putExtra("albumId", String.valueOf(quangCao.getIdAlbum()));
                        intent.putExtra("topic", "1");
                        context.startActivity(intent);


                        break;

                    case "artist":
                        // Chuyển đến trang chi tiết nghệ sĩ
                        Intent artistIntent = new Intent(context, ArtistActivity.class);
                        artistIntent.putExtra("artistId", Long.valueOf(quangCao.getIdNgheSi()));
                        context.startActivity(artistIntent);


                        break;
                }
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    private void playSongById(Long songId) {
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<GenericResponse<Song>> call = apiService.getSongById(songId);

        call.enqueue(new Callback<GenericResponse<Song>>() {
            @Override
            public void onResponse(Call<GenericResponse<Song>> call, Response<GenericResponse<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse<Song> genericResponse = response.body();
                    if (genericResponse.isSuccess() && genericResponse.getData() != null) {
                        Song song = genericResponse.getData();

                        // Tạo list chỉ chứa bài hát này
                        ArrayList<Song> singleSongList = new ArrayList<>();
                        singleSongList.add(song);

                        // Chuyển đổi thành MediaItem và phát
                        exoPlayerQueue.setCurrentQueue(SongToMediaItemHelper.convertToMediaItem(singleSongList));
                        exoPlayerQueue.setCurrentPosition(0);

                        // Chuyển sang SongDetailActivity
                        Intent intent = new Intent(context, SongDetailActivity.class);
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Song>> call, Throwable t) {
                Toast.makeText(context, "Không thể tải bài hát", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
