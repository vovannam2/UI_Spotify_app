package com.example.spotify_app.utils;

public class Const {
    private static String ACCESS_TOKEN = "";

    private static int value = 0;

    public static String getAccessToken() {
        return ACCESS_TOKEN;
    }

    public static void setAccessToken(String accessToken) {
        ACCESS_TOKEN = accessToken;
    }

    public static int getValue() {
        return value;
    }

    public static void setValue(int value) {
        Const.value = value;
    }
}
