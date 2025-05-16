package com.example.spotify_app.internals;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharePrefSearchHistory {
    private static final String PREF_NAME = "SearchHistoryPrefs";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private static final int MAX_HISTORY_ITEMS = 10;
    
    private final SharedPreferences preferences;
    private final Gson gson;

    public SharePrefSearchHistory(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addSearchQuery(String query) {
        List<String> history = getSearchHistory();
        
        // Remove existing entry if it exists
        history.remove(query);
        
        // Add new entry at the beginning
        history.add(0, query);
        
        // Keep only the most recent MAX_HISTORY_ITEMS
        if (history.size() > MAX_HISTORY_ITEMS) {
            history = history.subList(0, MAX_HISTORY_ITEMS);
        }
        
        saveSearchHistory(history);
    }

    public void removeSearchQuery(String query) {
        List<String> history = getSearchHistory();
        history.remove(query);
        saveSearchHistory(history);
    }

    public void clearSearchHistory() {
        preferences.edit().remove(KEY_SEARCH_HISTORY).apply();
    }

    public List<String> getSearchHistory() {
        String json = preferences.getString(KEY_SEARCH_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        
        try {
            Type type = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            // Nếu có lỗi khi đọc JSON, xóa dữ liệu cũ và trả về danh sách rỗng
            clearSearchHistory();
            return new ArrayList<>();
        }
    }

    private void saveSearchHistory(List<String> history) {
        String json = gson.toJson(history);
        preferences.edit().putString(KEY_SEARCH_HISTORY, json).apply();
    }
}
