package com.example.buckos.ui.feed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.models.User;

import java.util.List;

public class FollowSuggestionsAdapter extends RecyclerView.Adapter<FollowSuggestionsAdapter.ViewHolder> {

    private List<User> mFollowSuggestions;
    private Context mContext;

    @NonNull
    @Override
    public FollowSuggestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowSuggestionsAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mFollowSuggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
