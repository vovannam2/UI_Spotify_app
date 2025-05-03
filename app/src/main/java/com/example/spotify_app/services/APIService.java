package com.example.spotify_app.services;
import com.example.spotify_app.models.Album;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.Check;
import com.example.spotify_app.models.ForgotPassword;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.LoginRequest;
import com.example.spotify_app.models.LoginResponse;
import com.example.spotify_app.models.OtpResponse;
import com.example.spotify_app.models.RegisterRequest;
import com.example.spotify_app.models.RegisterResponse;
import com.example.spotify_app.models.ResetPasswordRequest;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongCommentRequest;
import com.example.spotify_app.models.SongCommentResponse;
import com.example.spotify_app.models.SongResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerModel);
    @GET("auth/register/confirm")
    Call<OtpResponse> verifyOtp(@Query("token") String token, @Query("type") String type);

    @POST("auth/authenticate")
    Call<LoginResponse> authenticate(@Body LoginRequest loginRequest);

    @POST("auth/send-email")
    Call<ResponseMessage> sendOtp(@Body ForgotPassword forgotPassword);

    @PATCH("user/forgot-password")
    Call<ResponseMessage> changePassword(@Body ResetPasswordRequest resetPasswordRequest);


    @GET("user/{id_user}/liked-songs")
    Call<GenericResponse<List<Song>>> getSongLikedByIdUser(@Path("id_user") int id_user);


    @POST("songLiked/toggle-like")
    Call<GenericResponse<Check>> toggleLike(@Query("songId") Long songId, @Query("userId") Long userId);

    @GET("song/most-views")
    Call<GenericResponse<SongResponse>> getMostViewSong(@Query("page") int page, @Query("size") int size  ,@Query("sort") String sort);

    @GET("albums")  // Cập nhật đúng endpoint
    Call<GenericResponse<List<Album>>> getAlbumHot();

    @GET("songs/search")
    Call<GenericResponse<List<Song>>> getSearchMusicList(@Query("query") String query);

    @PATCH("song/{id}/view")
    Call<GenericResponse<Song>> increaseViewOfSongBySongId(@Path("id") Long songId);


    @POST("song/comment/like")
    Call<GenericResponse<Boolean>> likeComment(@Query("commentId") Long commentId, @Query("userId") Long userId);

    @GET("song/{id_song}/comments")
    Call<SongCommentResponse> getAllCommentsOfSong(@Path("id_song") Long idSong);

    @GET("song/{songId}/artists")
    Call<GenericResponse<List<Artist>>> getArtistsBySongId(@Path("songId") Long songId);

    @GET("artist/{id}")
    Call<GenericResponse<Artist>> getArtistById(@Path("id") int id);
    @GET("artist/{idArtist}/songs/count")
    Call<GenericResponse<Integer>> getSongCountByArtistId(@Path("idArtist") int idArtist);

    @POST("song/post-comment")
    Call<ResponseMessage> postComment(@Body SongCommentRequest songCommentRequest);
}
