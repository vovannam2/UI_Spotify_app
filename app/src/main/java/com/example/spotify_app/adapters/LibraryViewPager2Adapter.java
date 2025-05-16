package com.example.spotify_app.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.spotify_app.fragments.UserFavouriteFragment;
import com.example.spotify_app.fragments.UserPlaylistFragment;


public class LibraryViewPager2Adapter extends FragmentStateAdapter {
    private static final int PLAYLIST_TAB_POSITION = 0;
    private static final int FAVOURITE_TAB_POSITION = 1;
    public LibraryViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == PLAYLIST_TAB_POSITION) {
            return new UserPlaylistFragment();
        } else {
            return new UserFavouriteFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
