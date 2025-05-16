package com.example.spotify_app.models;

import java.util.List;

public class ArtistResponse {
    private List<Artist> content;
    private int totalElements;
    private int totalPages;
    private boolean last;

    public List<Artist> getContent() {
        return content;
    }

    public void setContent(List<Artist> content) {
        this.content = content;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
