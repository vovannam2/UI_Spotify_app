package com.example.spotify_app.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class QuangCao implements Serializable {
    @SerializedName("idAdvertisement")
    @Expose
    private String idQuangCao;

    @SerializedName("image")
    @Expose
    private String hinhAnh;

    @SerializedName("content")
    @Expose
    private String noiDung;

    @SerializedName("type")
    @Expose
    private String loaiQuangCao; // "song", "album", "artist"

    // Thông tin bài hát
    @SerializedName("idSong")
    @Expose
    private String idBaiHat;

    @SerializedName("songName")
    @Expose
    private String tenBaiHat;

    @SerializedName("songImage")
    @Expose
    private String hinhBaiHat;

    // Thông tin album
    @SerializedName("idAlbum")
    @Expose
    private String idAlbum;

    @SerializedName("albumName")
    @Expose
    private String tenAlbum;

    @SerializedName("albumImage")
    @Expose
    private String hinhAlbum;

    // Thông tin nghệ sĩ
    @SerializedName("idArtist")
    @Expose
    private String idNgheSi;

    @SerializedName("artistName")
    @Expose
    private String tenNgheSi;

    @SerializedName("artistImage")
    @Expose
    private String hinhNgheSi;

    // Getters và Setters
    public String getIdQuangCao() {
        return idQuangCao;
    }

    public void setIdQuangCao(String idQuangCao) {
        this.idQuangCao = idQuangCao;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getLoaiQuangCao() {
        return loaiQuangCao;
    }

    public void setLoaiQuangCao(String loaiQuangCao) {
        this.loaiQuangCao = loaiQuangCao;
    }

    public String getIdBaiHat() {
        return idBaiHat;
    }

    public void setIdBaiHat(String idBaiHat) {
        this.idBaiHat = idBaiHat;
    }

    public String getTenBaiHat() {
        return tenBaiHat;
    }

    public void setTenBaiHat(String tenBaiHat) {
        this.tenBaiHat = tenBaiHat;
    }

    public String getHinhBaiHat() {
        return hinhBaiHat;
    }

    public void setHinhBaiHat(String hinhBaiHat) {
        this.hinhBaiHat = hinhBaiHat;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(String idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getTenAlbum() {
        return tenAlbum;
    }

    public void setTenAlbum(String tenAlbum) {
        this.tenAlbum = tenAlbum;
    }

    public String getHinhAlbum() {
        return hinhAlbum;
    }

    public void setHinhAlbum(String hinhAlbum) {
        this.hinhAlbum = hinhAlbum;
    }

    public String getIdNgheSi() {
        return idNgheSi;
    }

    public void setIdNgheSi(String idNgheSi) {
        this.idNgheSi = idNgheSi;
    }

    public String getTenNgheSi() {
        return tenNgheSi;
    }

    public void setTenNgheSi(String tenNgheSi) {
        this.tenNgheSi = tenNgheSi;
    }

    public String getHinhNgheSi() {
        return hinhNgheSi;
    }

    public void setHinhNgheSi(String hinhNgheSi) {
        this.hinhNgheSi = hinhNgheSi;
    }
}