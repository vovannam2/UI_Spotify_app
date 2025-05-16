package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.privacysandbox.ads.adservices.topics.Topic;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.ArtistActivity;
import com.example.spotify_app.activities.TopicActivity;
import com.example.spotify_app.adapters.ArtistAdapter;
import com.example.spotify_app.adapters.BaiHatHotAdapter;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.ArtistResponse;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoPlayerQueue;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_artists extends Fragment {
    View view;
    RecyclerView recyclerViewArtists;
    TextView xt_artirst;
    private ExoPlayerQueue exoPlayerQueue;
    ArtistAdapter artistAdapter;

    List<Song> songList;
    APIService apiService;

    List<Artist> artists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        exoPlayerQueue = ExoPlayerQueue.getInstance();
        view = inflater.inflate(R.layout.fragment_nghesi, container, false);
        artists = new ArrayList<>();
        artistAdapter = new ArtistAdapter(getContext(), new ArrayList<>(), artistItemClick);

        viewBinding();
        GetTopArtist();

        xt_artirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), TopicActivity.class);
                intent.putExtra("topic", "topArtist");
                startActivity(intent);
            }
        });

        return view;
    }
    private void viewBinding() {
        recyclerViewArtists = view.findViewById(R.id.rv_topartists);
        xt_artirst = view.findViewById(R.id.xemtatca_nghesihangdau);
    }
    private final ArtistAdapter.OnItemClickListener artistItemClick = new ArtistAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Artist artist) {
            Intent intent = new Intent(getContext(), ArtistActivity.class); //// sẽ thay thế bằng 1 actitvity khác
            intent.putExtra("artistId", artist.getIdUser());
            startActivity(intent);
        }
    };
    private void GetTopArtist(){
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getAllArtists(0, 5).enqueue(new Callback<GenericResponse<ArtistResponse>>() {
            @Override
            public void onResponse(Call<GenericResponse<ArtistResponse>> call, Response<GenericResponse<ArtistResponse>> response) {
                if (response.isSuccessful()) {
                    artists = response.body().getData().getContent();
                    Log.d("HomeFragment", "onResponse: Artist get succesfully " + artists.size());
                    artistAdapter.setArtistList(artists);
                    recyclerViewArtists.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false );
                    recyclerViewArtists.setLayoutManager(layoutManager);
                    recyclerViewArtists.setAdapter(artistAdapter);
                }
            }
            @Override
            public void onFailure(Call<GenericResponse<ArtistResponse>> call, Throwable t) {
                Log.d("ErrorReponse", t.getMessage());
            }
        });
    }
}
