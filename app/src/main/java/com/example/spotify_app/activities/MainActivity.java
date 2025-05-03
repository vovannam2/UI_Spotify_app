package com.example.spotify_app.activities;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.spotify_app.R;
import com.example.spotify_app.fragments.Fragment_Trang_Chu;
import com.example.spotify_app.fragments.Fragment_Thu_Vien;
import com.example.spotify_app.fragments.Fragment_Tim_Kiem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindingView();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, Fragment_Trang_Chu.newInstance())
                .commit();
        int fragmentId = getIntent().getIntExtra("fragmentId", -1);
        if (fragmentId != -1) {
            // Set the selected item to "Library"
            bottomNavigationView.setSelectedItemId(R.id.menu_item_library);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, Fragment_Thu_Vien.newInstance())
                    .commit();
        }
        setUpBottomNavigation();
    }

    private void bindingView() {
        // Bind the BottomNavigationView
        bottomNavigationView = findViewById(R.id.navigation);

    }

    private void setUpBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment;
                Log.d("CHECK_ID", "item.getItemId(): " + item.getItemId());
                Log.d("CHECK_ID", "R.id.menu_item_home: " + R.id.menu_item_home);
                // Thử sử dụng if-else thay vì switch
                int itemId = item.getItemId(); // Lấy ID của item
                if (itemId == R.id.menu_item_home) {
                    selectedFragment = Fragment_Trang_Chu.newInstance();
                } else if (itemId == R.id.menu_item_search) {
                    selectedFragment = Fragment_Tim_Kiem.newInstance();
                } else if (itemId == R.id.menu_item_library) {
                    selectedFragment = Fragment_Thu_Vien.newInstance();
                } else {
                    return false;
                }

                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, selectedFragment)
                        .commit();
                return true;

            }
        });


    }
}
