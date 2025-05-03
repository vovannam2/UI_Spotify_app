package com.example.spotify_app.utils;

import java.util.List;

public class Util {
    public static String covertToDate(List<Integer> day) {
        return String.valueOf(day.get(0)) + "-" + String.valueOf(day.get(1)) + "-" + String.valueOf(day.get(2));
    }
}
