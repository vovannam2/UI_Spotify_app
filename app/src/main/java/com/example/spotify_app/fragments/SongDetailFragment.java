package com.example.spotify_app.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.activities.BaseActivity;
import com.example.spotify_app.adapters.SongCommentAdapter;
import com.example.spotify_app.helpers.GradientHelper;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.SongComment;
import com.example.spotify_app.models.SongCommentRequest;
import com.example.spotify_app.models.SongCommentResponse;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.services.ExoBuilderService;
import com.example.spotify_app.services.ExoPlayerQueue;
import com.example.spotify_app.services.ExoService;
import com.example.spotify_app.services.SongViewManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SongDetailFragment extends BottomSheetDialogFragment {
    private Handler handler;
    private ExoPlayer exoPlayer;
    SeekBar seekBar;
    TextView tvSongTitle, tvArtistName, tvCurrentTime, tvDuration;
    MaterialButton btnRepeat, btnShuffle, btnPrevious, btnPlay, btnNext, publishCommentBtn;
    CircleImageView imSongAvt;
    EditText commentTxt;
    private FrameLayout overlay;
    private ProgressBar progressBar;
    View view;
    RecyclerView recyclerViewCmt;

    private ExoService exoService;
    private ExoBuilderService exoBuilderService;
    private SongCommentAdapter songCommentAdapter;
    private List<SongComment> songComments = new ArrayList<>();
    private ExoPlayerQueue exoPlayerQueue;
    private RotateAnimation rotateAnimation;
    private APIService apiService;
    private SongViewManager songViewManager;
    private User user;
    private View container;

    public SongDetailFragment() {

    }

    public static SongDetailFragment newInstance() {
        SongDetailFragment fragment = new SongDetailFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

        if (exoPlayerQueue.isShuffle()) {
            btnShuffle.setIconTint(getResources().getColorStateList(R.color.primary));
        } else {
            btnShuffle.setIconTint(getResources().getColorStateList(R.color.neutral3));
        }
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exoPlayerQueue.setShuffle(!exoPlayerQueue.isShuffle());
                if (exoPlayerQueue.isShuffle()) {
                    btnShuffle.setIconTint(getResources().getColorStateList(R.color.primary));
                } else {
                    btnShuffle.setIconTint(getResources().getColorStateList(R.color.neutral3));
                }
            }
        });


        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer != null) {
                    if (exoPlayerQueue.isShuffle()) {
                        MediaItem randomSong = exoPlayerQueue.getRandomUnplayedSong();
                        if (randomSong != null) {
                            exoPlayer.setMediaItem(randomSong);
                            exoPlayerQueue.addPlayedSong(randomSong);
                        }
                    } else {
                        exoPlayer.seekToPreviousMediaItem();
                    }
                }
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer != null) {
                    if (exoPlayer.isPlaying()) {
                        exoPlayer.pause();
                        btnPlay.setIcon(getResources().getDrawable(R.drawable.ic_32dp_filled_play));
                        imSongAvt.clearAnimation();
                    } else {
                        exoPlayer.play();
                        btnPlay.setIcon(getResources().getDrawable(R.drawable.ic_32dp_filled_pause));
                        imSongAvt.startAnimation(rotateAnimation);
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer != null) {
                    if (exoPlayerQueue.isShuffle()) {
                        MediaItem randomSong = exoPlayerQueue.getRandomUnplayedSong();
                        if (randomSong != null) {
                            exoPlayer.setMediaItem(randomSong);
                            exoPlayerQueue.addPlayedSong(randomSong);
                        }
                    } else if (exoPlayer.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                        exoPlayer.seekTo(0);
                    }
                    else {
                        exoPlayer.seekToNextMediaItem();
                    }
                }
            }
        });


        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int repeatMode = exoPlayer.getRepeatMode();
                if (repeatMode == Player.REPEAT_MODE_OFF) {
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                    btnRepeat.setIcon(getResources().getDrawable(R.drawable.ic_24dp_outline_repeat_on));
                    btnRepeat.setIconTint(getResources().getColorStateList(R.color.primary));
                } else if (repeatMode == Player.REPEAT_MODE_ALL){
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                    btnRepeat.setIcon(getResources().getDrawable(R.drawable.ic_24dp_filled_repeat_one));
                    btnRepeat.setIconTint(getResources().getColorStateList(R.color.primary));
                } else {
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    btnRepeat.setIcon(getResources().getDrawable(R.drawable.ic_24dp_outline_repeat_off));
                    btnRepeat.setIconTint(getResources().getColorStateList(R.color.neutral3));
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @OptIn(markerClass = UnstableApi.class) @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    exoPlayer.seekTo(progress);
                    tvCurrentTime.setText(Util.getStringForTime(new StringBuilder(), new Formatter(), progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(false);
                }
                btnPlay.setIcon(getResources().getDrawable(R.drawable.ic_32dp_filled_play));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(true);
                }
                btnPlay.setIcon(getResources().getDrawable(R.drawable.ic_32dp_filled_pause));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void hideOverlay() {
        overlay.setVisibility(View.INVISIBLE);
        overlay.setFocusable(false);
        overlay.setClickable(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void openOverlay() {
        overlay.setBackgroundColor(Color.argb(89, 0, 0, 0));
        overlay.setVisibility(View.VISIBLE);
        overlay.setFocusable(true);
        overlay.setClickable(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_song_detail, container, false);
        tvSongTitle = view.findViewById(R.id.tvSongTitle);
        tvArtistName = view.findViewById(R.id.tvArtistName);
        tvCurrentTime = view.findViewById(R.id.tvSongCurrentTime);
        tvDuration = view.findViewById(R.id.tvSongDuration);
        seekBar = view.findViewById(R.id.sbSongProgress);
        imSongAvt = view.findViewById(R.id.imSongAvt);
        btnRepeat = view.findViewById(R.id.btnRepeat);
        btnShuffle = view.findViewById(R.id.btnShuffle);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnNext = view.findViewById(R.id.btnNext);
        recyclerViewCmt = view.findViewById(R.id.recyclerViewCmt);
        user = SharePrefManagerUser.getInstance(getContext()).getUser();
        Glide.with(view.findViewById(R.id.commentLayout))
                .load(user.getAvatar())
                .into((CircleImageView) view.findViewById(R.id.avatarImageView));
        commentTxt = view.findViewById(R.id.commentTxt);
        publishCommentBtn = view.findViewById(R.id.publishCommentBtn);
        progressBar = view.findViewById(R.id.progressBar);
        overlay = view.findViewById(R.id.overlay);
        songViewManager = SongViewManager.getInstance();

        handler = new Handler();
        if (getActivity() instanceof BaseActivity) {
            exoPlayer = ((BaseActivity) getActivity()).getExoPlayer();
            playPlaylist();
        }

        songCommentAdapter = new SongCommentAdapter(getContext(), songComments, null);
        recyclerViewCmt.setAdapter(songCommentAdapter);
        recyclerViewCmt.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        recyclerViewCmt.setAdapter(songCommentAdapter);
        getAllComments();

        publishCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(String.valueOf(commentTxt.getText()).length() == 0) return;
                System.out.println(String.valueOf(commentTxt.getText()).length());
                openOverlay();
                postComment();
            }
        });
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();  // Đóng fragment, quay về activity phía sau
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Khởi tạo các view
        container = view.findViewById(R.id.container);
        
        // Lấy thông tin bài hát hiện tại
        ExoPlayer exoPlayer = ((BaseActivity) requireActivity()).getExoPlayer();
        if (exoPlayer != null && exoPlayer.getCurrentMediaItem() != null) {
            MediaMetadata metadata = exoPlayer.getCurrentMediaItem().mediaMetadata;
            if (metadata != null && metadata.artworkUri != null) {
                // Áp dụng gradient dựa trên ảnh bài hát
                GradientHelper.applyDoubleGradient(requireContext(), container, String.valueOf(metadata.artworkUri));
            }
        }
        
        // ... rest of your existing code ...
    }

    private void postComment() {
        SongCommentRequest req = new SongCommentRequest();
        req.setIdSong(exoPlayer.getCurrentMediaItem().mediaMetadata.extras.getLong("id"));
        user = SharePrefManagerUser.getInstance(getContext()).getUser();
        req.setIdUser((long) user.getId());
        req.setContent(String.valueOf(commentTxt.getText()));
        commentTxt.setText("");
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.postComment(req).enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {
                hideOverlay();
                ResponseMessage res = response.body();
                assert res != null;
                getAllComments();
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {
                hideOverlay();
            }
        });

    }

    @OptIn(markerClass = UnstableApi.class) private void getAllComments() {
        if(exoPlayer == null) return;
        Long idSong = exoPlayer.getCurrentMediaItem().mediaMetadata.extras.getLong("id");
        Log.d("Thanh122223", String.valueOf(idSong));
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getAllCommentsOfSong(idSong).enqueue(new Callback<SongCommentResponse>() {
            @Override
            public void onResponse(Call<SongCommentResponse> call, Response<SongCommentResponse> response) {
                SongCommentResponse res = response.body();
                assert res != null;
                if(res.isSuccess()) {
                    songComments = res.getData();
                    songCommentAdapter.setSongComments(songComments);
                }
            }

            @Override
            public void onFailure(Call<SongCommentResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayer != null) {
            if (exoPlayer.getCurrentMediaItem() != null) {
                MediaItem currentSong = exoPlayer.getCurrentMediaItem();
                MediaMetadata metadata = currentSong.mediaMetadata;
                updateSongAsset(metadata);
                handler.post(updateMediaRunable);
                btnPlay.performClick();
                btnPlay.performClick();
            }
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void playPlaylist() {
        if (exoPlayerQueue == null) {
            exoPlayerQueue = ExoPlayerQueue.getInstance();
        }
        long currentSongId = -1;
        if(exoPlayer == null) return;
        if (exoPlayer.getCurrentMediaItem() != null && exoPlayer.getCurrentMediaItem().mediaMetadata.extras != null) {
            currentSongId = exoPlayer.getCurrentMediaItem().mediaMetadata.extras.getLong("id");
        }
        long newSongId = exoPlayerQueue.getCurrentMediaItem().mediaMetadata.extras.getLong("id");
        Log.d("Compare", "playPlaylist: " + currentSongId + " " + newSongId);
        if (currentSongId != newSongId) {
            exoPlayer.clearMediaItems();
            if (exoPlayerQueue.isShuffle()) exoPlayerQueue.shuffle();
            List<MediaItem> queue = exoPlayerQueue.getCurrentQueue();
            int currentPos = exoPlayerQueue.getCurrentPosition();
            exoPlayer.setMediaItems(queue);
            exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
            exoPlayer.prepare();
            exoPlayer.seekTo(currentPos, 0);
            exoPlayer.play();
            updateSongAsset(exoPlayerQueue.getCurrentMediaItem().mediaMetadata);
        } else {
            if (!exoPlayer.isPlaying()) exoPlayer.play();
        }

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    long duration = exoPlayer.getDuration();
                    seekBar.setMax((int) duration);
                    tvDuration.setText(Util.getStringForTime(new StringBuilder(), new Formatter(), duration));
                    handler.post(updateMediaRunable);
                } else {
                    handler.removeCallbacks(updateMediaRunable);
                }
            }
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    if (exoPlayerQueue.isShuffle()) {
                        MediaItem randomSong = exoPlayerQueue.getRandomUnplayedSong();
                        if (randomSong != null) {
                            exoPlayer.setMediaItem(randomSong);
                            exoPlayerQueue.addPlayedSong(randomSong);
                        }
                    }
                }
                if (mediaItem != null) {
                    MediaMetadata metadata = mediaItem.mediaMetadata;
                    updateSongAsset(metadata);
                    getAllComments();
                }
            }
        });
        btnPlay.setIcon(getResources().getDrawable(R.drawable.ic_32dp_filled_pause));
    }

    private Runnable updateMediaRunable = new Runnable() {
        @OptIn(markerClass = UnstableApi.class) @Override
        public void run() {
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                long current = exoPlayer.getCurrentPosition();
                seekBar.setProgress((int) current);
                tvCurrentTime.setText(Util.getStringForTime(new StringBuilder(), new Formatter(), current));
                handler.postDelayed(this, 1000);
            }
        }
    };


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    // Thêm BottomSheetCallback để lắng nghe sự kiện kéo
                    behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int newState) {
                            if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {

                            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                                dismiss();
                            }
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                        }
                    });
                    ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        bottomSheet.setLayoutParams(layoutParams);
                    }
                }
            }
        });
        return dialog;
    }



    @OptIn(markerClass = UnstableApi.class) private void updateSongAsset(MediaMetadata metadata) {
        if (isAdded()) {

            tvSongTitle.setText(metadata.title);
            tvArtistName.setText(metadata.extras.getString("artist"));
            tvDuration.setText(Util.getStringForTime(new StringBuilder(), new Formatter(), exoPlayer.getDuration()));
            tvCurrentTime.setText(Util.getStringForTime(new StringBuilder(), new Formatter(), exoPlayer.getCurrentPosition()));

            Glide.with(getContext())
                    .load(metadata.artworkUri)
                    .transform(new RoundedCornersTransformation(30, 0))
                    .into(imSongAvt);
            GradientHelper.applyGradient(getContext(), view, String.valueOf(metadata.artworkUri));

            rotateAnimation = new RotateAnimation(
                    0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            rotateAnimation.setDuration(20000);
            rotateAnimation.setRepeatCount(Animation.INFINITE);

            imSongAvt.startAnimation(rotateAnimation);
        }
    }

    public ExoPlayer getExoPlayer() {
        return exoPlayer;
    }
}