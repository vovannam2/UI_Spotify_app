package com.example.spotify_app.models;

import java.util.List;

public class SongComment {
    private Long idComment;
    private String content;
    private int likes;
    private User user;
    private List<Long> listUserLike;

    private List<Integer> dayCommented;

    public Long getIdComment() {
        return idComment;
    }

    public void setIdComment(Long idComment) {
        this.idComment = idComment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Integer> getDayCommented() {
        return dayCommented;
    }

    public void setDayCommented(List<Integer> dayCommented) {
        this.dayCommented = dayCommented;
    }

    public List<Long> getListUserLike() {
        return listUserLike;
    }

    public void setListUserLike(List<Long> listUserLike) {
        this.listUserLike = listUserLike;
    }
}
