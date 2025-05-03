//Fragment tim kiem
package com.example.spotify_app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.spotify_app.R;
import com.example.spotify_app.adapters.SearchMusicAdapter;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tim_Kiem extends Fragment {
    View view;
    Toolbar toolbar;
    RecyclerView recyclerViewSearchMusic;
    TextView tvNoDataWhenSearch;
    SearchMusicAdapter searchMusicAdapter;
    APIService apiService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeForSearch);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        view = localInflater.inflate(R.layout.fragment_tim_kiem, container, false);
        viewBinding(view);
        return view;
    }

    private void viewBinding(View view) {
        toolbar = view.findViewById(R.id.toolBarSearchMusic);
        recyclerViewSearchMusic = view.findViewById(R.id.recyclerViewSearchMusic);
        tvNoDataWhenSearch = view.findViewById(R.id.tvNoResultForMusic);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        toolbar.setTitle("");
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_view, menu);
        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Tìm kiếm...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMusicKeyword(query);
                /*Log.d("BBB", query);*/
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchMusicKeyword(String query){
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<GenericResponse<List<Song>>> callback = apiService.getSearchMusicList(query);
        callback.enqueue(new Callback<GenericResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Song>>> call, Response<GenericResponse<List<Song>>> response) {
                List listsong = response.body().getData();
                ArrayList<Song> baiHatArrayList = new ArrayList<>(listsong);
                if(baiHatArrayList.size() > 0){
                    searchMusicAdapter = new SearchMusicAdapter(getActivity(), baiHatArrayList);
                    recyclerViewSearchMusic.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerViewSearchMusic.setAdapter(searchMusicAdapter);
                    tvNoDataWhenSearch.setVisibility(View.GONE);
                    recyclerViewSearchMusic.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewSearchMusic.setVisibility(View.GONE);
                    tvNoDataWhenSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Song>>> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to load data. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static Fragment_Tim_Kiem newInstance() {
        Fragment_Tim_Kiem fragment = new Fragment_Tim_Kiem();
        return fragment;
    }
}
