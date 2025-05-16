package com.example.spotify_app.models;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Playlist {
    @SerializedName("idPlaylist")
    private int idPlaylist;

    @SerializedName("idUser")
    private Long idUser;
    @SerializedName("name")
    private String name;
    @SerializedName("dayCreated")
    private String dayCreated;
    @SerializedName("image")
    private String image;
    @SerializedName("songs")
    private List<Song> songs;

    public Playlist() {
    }

    public Playlist(int idPlaylist, Long idUser, String name, String dayCreated, String image, List<Song> songs) {
        this.idPlaylist = idPlaylist;
        this.idUser = idUser;
        this.name = name;
        this.dayCreated = dayCreated;
        this.image = image;
        this.songs = songs;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDayCreated() {
        if (dayCreated == null) return null;
        return LocalDateTime.parse(dayCreated, DateTimeFormatter.ISO_DATE_TIME);
    }

    public void setDayCreated(String dayCreated) {
        this.dayCreated = dayCreated;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public int getSongCount() {
        return songs == null ? 0 : songs.size();
    }
}
