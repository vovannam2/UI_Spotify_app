package com.example.spotify_app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListPlaylistResponse {
    @SerializedName("success")
    private Boolean success;
    @SerializedName("error")
    private Boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Playlist> data;

    public ListPlaylistResponse() {
    }

    public ListPlaylistResponse(Boolean success, Boolean error, String message, List<Playlist> data) {
        this.success = success;
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Playlist> getData() {
        return data;
    }

    public void setData(List<Playlist> data) {
        this.data = data;
    }
}
