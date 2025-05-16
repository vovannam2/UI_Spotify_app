package com.example.spotify_app.models;

import com.google.gson.annotations.SerializedName;

public class PlaylistResponse {
    @SerializedName("success")
    private Boolean success;
    @SerializedName("error")
    private Boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private Playlist data;

    public PlaylistResponse() {
    }

    public PlaylistResponse(Boolean success, Boolean error, String message, Playlist data) {
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

    public Playlist getData() {
        return data;
    }

    public void setData(Playlist data) {
        this.data = data;
    }
}
