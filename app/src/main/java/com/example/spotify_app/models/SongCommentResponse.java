package com.example.spotify_app.models;

import java.util.List;

public class SongCommentResponse {
    private boolean success;
    private boolean error;
    private String message;
    private List<SongComment> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SongComment> getData() {
        return data;
    }

    public void setData(List<SongComment> data) {
        this.data = data;
    }
}
