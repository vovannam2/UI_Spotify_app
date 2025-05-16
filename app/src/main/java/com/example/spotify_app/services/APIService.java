package com.example.spotify_app.services;
import com.example.spotify_app.models.Album;
import com.example.spotify_app.models.Artist;
import com.example.spotify_app.models.ArtistResponse;
import com.example.spotify_app.models.Check;
import com.example.spotify_app.models.ForgotPassword;
import com.example.spotify_app.models.GenericResponse;
import com.example.spotify_app.models.ListPlaylistResponse;
import com.example.spotify_app.models.LoginRequest;
import com.example.spotify_app.models.LoginResponse;
import com.example.spotify_app.models.OtpResponse;
import com.example.spotify_app.models.PlaylistRequest;
import com.example.spotify_app.models.PlaylistResponse;
import com.example.spotify_app.models.QuangCao;
import com.example.spotify_app.models.RegisterRequest;
import com.example.spotify_app.models.RegisterResponse;
import com.example.spotify_app.models.ResetPasswordRequest;
import com.example.spotify_app.models.ResponseMessage;
import com.example.spotify_app.models.Song;
import com.example.spotify_app.models.SongCommentRequest;
import com.example.spotify_app.models.SongCommentResponse;
import com.example.spotify_app.models.SongLikedRequest;
import com.example.spotify_app.models.SongLikedResponse;
import com.example.spotify_app.models.SongResponse;
import com.example.spotify_app.models.UserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    Call<GenericResponse<Boolean>> toggleLike(@Query("songId") Long songId, @Query("userId") Long userId);


    @GET("song/most-views")
    Call<GenericResponse<SongResponse>> getMostViewSong(@Query("page") int page, @Query("size") int size  ,@Query("sort") String sort);

    @GET("albums")  // Cập nhật đúng endpoint
    Call<GenericResponse<List<Album>>> getAlbumHot();

    @GET("advertisements")  // Cập nhật đúng endpoint
    Call<GenericResponse<List<QuangCao>>> getadvertisements();

    @GET("songs/search")
    Call<GenericResponse<List<Song>>> getSearchMusicList(@Query("query") String query);

    @PATCH("song/{id}/view")
    Call<GenericResponse<Song>> increaseViewOfSongBySongId(@Path("id") Long songId);
    @GET("song/{id}")
    Call<GenericResponse<Song>> getSongById(@Path("id") Long id);

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

    @GET("song/most-likes")
    Call<GenericResponse<SongResponse>> getMostLikeSong(@Query("page") int page, @Query("size") int size);

    @GET("song/new-released")
    Call<GenericResponse<SongResponse>> getSongNewReleased(@Query("page") int page, @Query("size") int size);

    @GET("album/{id_album}")
    Call<GenericResponse<Album>> getAlbumById(@Path("id_album") int id_album);

    @GET("user/{id_user}/playlists")
    Call<ListPlaylistResponse> getPlaylistByIdUser(@Path("id_user") int id_user);

    @GET("playlist/{id_playlist}")
    Call<PlaylistResponse> getPlaylistById(@Path("id_playlist") int id_playlist);

    @GET("playlist")
    Call<ResponseMessage> isPlaylistNameExists(@Query("name") String name);

    @POST("playlist/{id_playlist}")
    Call<ResponseMessage> updatePlaylistName(@Path("id_playlist") int i, @Query("name") String name);

    @DELETE("playlist/{id_playlist}")
    Call<ResponseMessage> deletePlaylist(@Path("id_playlist") int id_playlist);

    @GET("songLiked/isUserLikedSong")
    Call<SongLikedResponse> isUserLikedSong(@Query("songId") Long songId, @Query("userId") Long userId);

    @DELETE("playlistSong/{id_playlist}/{id_song}")
    Call<ResponseMessage> deleteSongFromPlaylist(@Path("id_playlist") Long id_playlist, @Path("id_song") Long id_song);

    @POST("playlistSong/{id_playlist}/{id_song}")
    Call<ResponseMessage> addSongToPlaylist(@Path("id_playlist") Long id_playlist, @Path("id_song") Long id_song);


    @GET("artists")
    Call<GenericResponse<ArtistResponse>> getAllArtists(@Query("page") int page, @Query("size") int size);

    @GET("user/searchArtist")
    Call<GenericResponse<List<Artist>>> searchArtist(@Query("query") String query);

    @GET("user/{id_user}/is-followed-artist")
    Call<GenericResponse<Boolean>> isFollowedArtist(@Path("id_user") int id_user, @Query("id_artist") int id_artist);

    @POST("user/{id_user}/follow-artist")
    Call<GenericResponse<Boolean>> followArtist(@Path("id_user") int id_user, @Query("id_artist") int id_artist);

    @GET("artist/{artistId}/songs")
    Call<GenericResponse<SongResponse>> getAllSongsByArtistId(@Path("artistId") int artistId, @Query("page") int page, @Query("size") int size);

    @GET("albums/artist/{id_artist}")
    Call<GenericResponse<List<Album>>> getAlbumsByArtistId(@Path("id_artist") int id_artist);

    @POST("playlist")
    Call<PlaylistResponse> createPlaylist(@Body PlaylistRequest playlistRequest);

    @GET("songs")
    Call<GenericResponse<List<Song>>> getAllSongs();

    @GET("user/{id_user}/not-liked-songs")
    Call<GenericResponse<List<Song>>> getNotLikedSongsByIdUser(@Path("id_user") int id_user);

    @POST("songLiked/songs")
    Call<ResponseMessage> addSongsToFavourite(@Body SongLikedRequest songLikedRequest);


    @Multipart
    @PATCH("user/update")
    Call<UserResponse> updateProfile(@Part("idUser") Long idUser, @Part MultipartBody.Part imageFile, @Part("firstName") String firstName, @Part("lastName") String lastName, @Part("gender") int gender);

}
