package com.example.spotify_app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import com.example.spotify_app.R;
import com.example.spotify_app.adapters.BannerAdapter;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.QuangCao;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentBanner extends Fragment {
    View view;
    ViewPager viewPager;
    CircleIndicator circleIndicator;
    BannerAdapter bannerAdapter;
    Runnable runnable;
    Handler handler;
    int currentItem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_banner, container, false);

        bindingView();
        getData();
        return view;
    }

    private void bindingView() {
        viewPager = view.findViewById(R.id.viewPager);
        circleIndicator = view.findViewById(R.id.indicatorDefault);
    }

    private void getData() {
        Log.d("nguyen ngoc khanh luy","Đã vopp  ldsnfds");
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<GenericResponse<List<QuangCao>>> callback = apiService.getadvertisements();

        callback.enqueue(new Callback<GenericResponse<List<QuangCao>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<QuangCao>>> call, Response<GenericResponse<List<QuangCao>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse<List<QuangCao>> genericResponse = response.body();

                    if (genericResponse.isSuccess() && genericResponse.getData() != null) {
                        List<QuangCao> banners = genericResponse.getData();
                        ArrayList<QuangCao> bannersarrr = new ArrayList<>(banners);

                        bannerAdapter = new BannerAdapter(getActivity(), bannersarrr);
                        viewPager.setAdapter(bannerAdapter);
                        circleIndicator.setViewPager(viewPager);

                        if (handler == null) {
                            handler = new Handler(Looper.getMainLooper());
                        }

                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                int itemCount = viewPager.getAdapter() != null ? viewPager.getAdapter().getCount() : 0;
                                if (itemCount > 0) {
                                    currentItem = (viewPager.getCurrentItem() + 1) % itemCount;
                                    viewPager.setCurrentItem(currentItem, true);
                                    handler.postDelayed(this, 4500);
                                }
                            }
                        };

                        handler.postDelayed(runnable, 4500);
                    } else {
                        Log.e("Banner", "API returned error or no data: " + genericResponse.getMessage());
                        Log.e("onResponse", "Response không thành công: " + response.message());
                        Toast.makeText(getActivity(), "Failed to load data.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Banner", "Response not successful or empty body");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<QuangCao>>> call, Throwable t) {
                Log.e("Banner", "Failed to fetch advertisements", t);
            }
        });
    }



}
