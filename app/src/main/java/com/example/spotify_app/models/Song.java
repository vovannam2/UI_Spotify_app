package com.example.spotify_app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Song {
    @SerializedName("idSong")
    private Long idSong;
    @SerializedName("name")
    private String name;
    @SerializedName("views")
    private int views;
    @SerializedName("dayCreated")
    private String dayCreated;
    @SerializedName("resource")
    private String resource;
    @SerializedName("image")
    private String image;
    @SerializedName("artistId")
    private Long artistId;
    @SerializedName("artistName")
    private String artistName;

    public Song() {
    }

    public Song(Long idSong, String name, int views, String dayCreated, String resource, String image, Long artistId, String artistName) {
        this.idSong = idSong;
        this.name = name;
        this.views = views;
        this.dayCreated = dayCreated;
        this.resource = resource;
        this.image = image;
        this.artistId = artistId;
        this.artistName = artistName;
    }

    public Long getIdSong() {
        return idSong;
    }

    public void setIdSong(Long idSong) {
        this.idSong = idSong;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getDayCreated() {
        return dayCreated;
    }

    public void setDayCreated(String dayCreated) {
        this.dayCreated = dayCreated;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
