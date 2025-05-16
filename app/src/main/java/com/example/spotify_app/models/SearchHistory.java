package com.example.spotify_app.models;

import java.util.Date;

public class SearchHistory {
    private String query;
    private Date timestamp;

    public SearchHistory(String query) {
        this.query = query;
        this.timestamp = new Date();
    }

    public String getQuery() {
        return query;
    }

    public Date getTimestamp() {
        return timestamp;
    }
} 