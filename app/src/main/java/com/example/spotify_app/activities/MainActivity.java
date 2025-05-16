package com.example.spotify_app.activities;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.spotify_app.R;
import com.example.spotify_app.fragments.Fragment_Trang_Chu;
import com.example.spotify_app.fragments.Fragment_Thu_Vien;
import com.example.spotify_app.fragments.Fragment_Tim_Kiem;

import com.example.spotify_app.fragments.LibraryFragment;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.User;
import com.example.spotify_app.utils.Const;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;

    FragmentManager fragmentManager;

    private SlidingRootNav slidingRootNavBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindingView();
        initMiniPlayer();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, Fragment_Trang_Chu.newInstance())
                .commit();
        int fragmentId = getIntent().getIntExtra("fragmentId", -1);
        if (fragmentId != -1) {
            if(fragmentId == 5){
                bottomNavigationView.setSelectedItemId(R.id.menu_item_search);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, Fragment_Tim_Kiem.newInstance())
                        .commit();
            }else {
                bottomNavigationView.setSelectedItemId(R.id.menu_item_library);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_frame_layout, LibraryFragment.newInstance())
                        .commit();
            }
            // Set the selected item to "Library"

        }


        setUpBottomNavigation();

        /*User user = new User();
        user.setFirstName("Nam");
        user.setLastName("Tester");
        user.setEmail("nam@example.com");
        user.setAvatar("https://i.postimg.cc/jdhRqVw6/avarta.jpg");
        user.setId(2);
        user.setAccessToken("test-token");
        user.setRefreshToken("refresh-token");
        user.setProvider("local");*/

        /*SharePrefManagerUser.getInstance(getApplicationContext()).loginSuccess(user);*/
        slidingRootNavBuilder = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.main_drawer_layout)
                .withContentClickableWhenMenuOpened(false)
                .withDragDistance(250)
                .inject();
        // Đảm bảo menu đóng ban đầu


        onDrawerItemsClicked();
        slidingRootNavBuilder.closeMenu();




    }
    private void onDrawerItemsClicked() {
        if (slidingRootNavBuilder == null) return;

        // Gán sự kiện cho các item trong menu trượt
        slidingRootNavBuilder.getLayout().findViewById(R.id.settings).setOnClickListener(v -> {
            // Xử lý mở Settings
            startActivity(new Intent(this, SettingActivity.class));
            slidingRootNavBuilder.closeMenu();
        });

        slidingRootNavBuilder.getLayout().findViewById(R.id.logo).setOnClickListener(view -> slidingRootNavBuilder.closeMenu());

        slidingRootNavBuilder.getLayout().findViewById(R.id.library).setOnClickListener(view -> {
            // Xử lý mở Library
            startActivity(new Intent(this, ProfileActivity.class));
            slidingRootNavBuilder.closeMenu();
        });

        slidingRootNavBuilder.getLayout().findViewById(R.id.about).setOnClickListener(view -> {
            // Xử lý mở About
            // startActivity(new Intent(this, AboutActivity.class));
            slidingRootNavBuilder.closeMenu();
        });
    }

    private void bindingView() {
        // Bind the BottomNavigationView
        bottomNavigationView = findViewById(R.id.navigation);

    }
    public void openDrawerMenu() {
        if (slidingRootNavBuilder != null) {
            slidingRootNavBuilder.openMenu(true);
        }
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
                    selectedFragment = LibraryFragment.newInstance();
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

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}
