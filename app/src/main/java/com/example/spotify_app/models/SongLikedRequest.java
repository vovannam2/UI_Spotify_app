package com.example.spotify_app.models;

import java.util.List;

public class SongLikedRequest {
    private Long idUser;
    private List<Long> songIds;

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public List<Long> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }
}
