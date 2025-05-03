package com.example.spotify_app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.spotify_app.R;
import com.example.spotify_app.internals.SharePrefManagerUser;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.SongComment;
import com.example.spotify_app.models.User;
import com.example.spotify_app.retrofit.RetrofitClient;
import com.example.spotify_app.services.APIService;
import com.example.spotify_app.utils.Util;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongCommentAdapter extends RecyclerView.Adapter<SongCommentAdapter.SongCommentViewHolder> {

    private final Context context;
    private List<Long> listUserLike;

    private List<SongComment> songComments;
    private final OnItemClickListener onItemClickListener;
    private User user;
    APIService apiService;

    public SongCommentAdapter(Context context, List<SongComment> songComments, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.songComments = songComments;
        this.onItemClickListener = onItemClickListener;
    }

    public void setSongComments(List<SongComment> songComments) {
        this.songComments = songComments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_comment, parent, false);
        return new SongCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongCommentViewHolder holder, int position) {
        SongComment songComment = songComments.get(position);
        if(songComment == null) return;
        holder.nameTxt.setText(songComment.getUser().getFirstName() + " " + songComment.getUser().getLastName());
        Glide.with(context).load(songComment.getUser().getAvatar()).into(holder.avatarImageView);
        holder.contentTxt.setText(songComment.getContent());
        holder.likeCntTxt.setText(String.valueOf(songComment.getLikes()));
        holder.timeTxt.setText(Util.covertToDate(songComment.getDayCommented()));

        Log.d("SongCommentAdapter", "onBindViewHolder: " + songComment.getListUserLike().size());
        boolean isLiked = false;
        for (Long i : songComment.getListUserLike()) {
            if (user.getId() == i) {
                isLiked = true;

                break;
            }
        }
        if (isLiked) {
            holder.btnLike.setIconResource(R.drawable.ic_24dp_filled_favorite);
        } else {
            holder.btnLike.setIconResource(R.drawable.ic_24dp_outline_favorite);
        }
    }

    @Override
    public int getItemCount() {
        return songComments == null ? 0 : songComments.size();
    }

    public class SongCommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarImageView;
        TextView nameTxt, contentTxt, likeCntTxt, timeTxt;
        MaterialButton btnLike;

        public SongCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            contentTxt = itemView.findViewById(R.id.contentTxt);
            likeCntTxt = itemView.findViewById(R.id.likeCntTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            btnLike = itemView.findViewById(R.id.btnLikeComment);

            apiService = RetrofitClient.getRetrofit().create(APIService.class);
            user = SharePrefManagerUser.getInstance(context).getUser();

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    apiService.likeComment(songComments.get(getAdapterPosition()).getIdComment(), (long) user.getId()).enqueue(new Callback<GenericResponse<Boolean>>() {
                        @Override
                        public void onResponse(Call<GenericResponse<Boolean>> call, Response<GenericResponse<Boolean>> response) {
                            if (response.isSuccessful()) {
                                boolean isLiked = response.body().getData();
                                if (isLiked) {
                                    btnLike.setIconResource(R.drawable.ic_24dp_filled_favorite);
                                    songComments.get(getAdapterPosition()).setLikes(songComments.get(getAdapterPosition()).getLikes() + 1);
                                    likeCntTxt.setText(String.valueOf(songComments.get(getAdapterPosition()).getLikes()));
                                } else {
                                    btnLike.setIconResource(R.drawable.ic_24dp_outline_favorite);
                                    songComments.get(getAdapterPosition()).setLikes(songComments.get(getAdapterPosition()).getLikes() - 1);
                                    likeCntTxt.setText(String.valueOf(songComments.get(getAdapterPosition()).getLikes()));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GenericResponse<Boolean>> call, Throwable t) {

                        }
                    });
                }
            });
        }

    }

    public List<Long> getListUserLike() {
        return listUserLike;
    }

    public void setListUserLike(List<Long> listUserLike) {
        this.listUserLike = listUserLike;
    }

    public interface OnItemClickListener {
    }
}
