package com.example.spotify_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify_app.R;
import com.example.spotify_app.models.SearchHistory;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<String> historyList;
    private OnItemClickListener listener;
    private OnRemoveClickListener removeListener;

    public interface OnItemClickListener {
        void onItemClick(String query);
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(String query);
    }

    public SearchHistoryAdapter(List<String> historyList, OnItemClickListener listener, OnRemoveClickListener removeListener) {
        this.historyList = historyList;
        this.listener = listener;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = historyList.get(position);
        holder.tvSearchQuery.setText(query);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(query);
            }
        });

        holder.btnRemoveHistory.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemoveClick(query);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<String> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSearchQuery;
        ImageButton btnRemoveHistory;

        ViewHolder(View itemView) {
            super(itemView);
            tvSearchQuery = itemView.findViewById(R.id.tvSearchQuery);
            btnRemoveHistory = itemView.findViewById(R.id.btnRemoveHistory);
        }
    }
}