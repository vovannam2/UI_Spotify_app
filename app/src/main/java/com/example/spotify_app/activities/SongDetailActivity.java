package com.example.spotify_app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.FragmentTransaction;
import androidx.media3.common.MediaMetadata;

import com.example.spotify_app.R;
import com.example.spotify_app.fragments.SongDetailFragment;


public class SongDetailActivity extends BaseActivity {
    private SongDetailFragment songDetailFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (songDetailFragment != null) {
                   startActivity(new Intent(SongDetailActivity.this, MainActivity.class));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Chờ cho đến khi exoPlayer đã được khởi tạo
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (getExoPlayer() == null) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                        if (songDetailFragment != null) {
                            songDetailFragment.getExoPlayer().stop();
                            fragmentTransaction.hide(songDetailFragment).commit();
                        }
                        songDetailFragment = SongDetailFragment.newInstance();
                        if (songDetailFragment.isAdded()) {
                            fragmentTransaction.replace(R.id.fragment_container, songDetailFragment, "SongDetailFragment").addToBackStack(null);
                        } else {
                            fragmentTransaction.add(R.id.fragment_container, songDetailFragment, "SongDetailFragment").addToBackStack(null);
                        }
                        if(getExoPlayer() != null) {
                            fragmentTransaction.commit();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void updateMiniplayer(MediaMetadata metadata) {

    }

}