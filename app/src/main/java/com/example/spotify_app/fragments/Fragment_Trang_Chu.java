package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spotify_app.R;
import com.example.spotify_app.activities.AddSongToPlaylistActivity;
import com.example.spotify_app.activities.MainActivity;
import com.example.spotify_app.activities.SearchActivity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;



public class Fragment_Trang_Chu extends Fragment {

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        EdgeToEdge.enable(requireActivity());
        // Set up profile icon click listener
        ImageView profileIcon = view.findViewById(R.id.profile_icon);
        profileIcon.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDrawerMenu();
            }
        });
        ImageView  searchicon = view.findViewById(R.id.search);
        searchicon.setOnClickListener(v -> {
            openSearch(view);
        });


        return view;
    }
    public static Fragment_Trang_Chu newInstance() {
        Fragment_Trang_Chu fragment = new Fragment_Trang_Chu();
        return fragment;
    }
    public void openSearch(View view) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame_layout, Fragment_Tim_Kiem.newInstance())
                    .commit();
            // Update bottom navigation selection
            mainActivity.getBottomNavigationView().setSelectedItemId(R.id.menu_item_search);
        }
    }

}
