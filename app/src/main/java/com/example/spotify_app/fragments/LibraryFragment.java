package com.example.spotify_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.spotify_app.activities.BaseActivity;
import com.example.spotify_app.adapters.LibraryViewPager2Adapter;
import com.example.spotify_app.databinding.FragmentLibraryBinding;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.User;
import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class LibraryFragment extends Fragment {
    private FragmentLibraryBinding binding;
    User user;

    public LibraryFragment() {
        // Required empty public constructor
    }

    public static LibraryFragment newInstance() {
        LibraryFragment fragment = new LibraryFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        bindingView(inflater, container);
        handleTabLayout();
        handleUserProfile();
        handleButtonNotification();
        handleButtonSetting();
        return binding.getRoot();
    }

    private void bindingView(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        LibraryViewPager2Adapter adapter = new LibraryViewPager2Adapter(fragmentManager, getLifecycle());
        binding.libraryViewPager.setAdapter(adapter);
        CircleImageView imvUserAvatar = binding.includedTopAppBar.imvUserAvatar;
        user = SharePrefManagerUser.getInstance(this.getContext()).getUser();
        Glide.with(this.getContext())
                .load(user.getAvatar())
                .into(imvUserAvatar);
    }

    private void handleTabLayout() {
        binding.libraryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                binding.libraryViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        }) ;

        binding.libraryViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.libraryTabLayout.selectTab(binding.libraryTabLayout.getTabAt(position));
            }
        });
    }

    private void handleUserProfile() {
        binding.includedTopAppBar.imvUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BaseActivity.class); /// the tam thoi ProfileActivity
                startActivity(intent);
            }
        });
    }

    private void handleButtonNotification() {
        binding.includedTopAppBar.btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BaseActivity.class); /// NotificationActivity
                startActivity(intent);
            }
        });
    }

    private void handleButtonSetting() {
        binding.includedTopAppBar.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BaseActivity.class);  /// SettingActivity
                startActivity(intent);
            }
        });
    }
}