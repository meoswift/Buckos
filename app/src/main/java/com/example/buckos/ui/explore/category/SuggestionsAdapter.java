package com.example.buckos.ui.explore.category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Story;
import com.example.buckos.models.Suggestion;
import com.example.buckos.ui.create.NewItemActivity;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    private List<Suggestion> mSuggestions;
    private Context mContext;

    public SuggestionsAdapter(Context context, List<Suggestion> suggestions) {
        mSuggestions = suggestions;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Suggestion suggestion = mSuggestions.get(position);

        holder.suggestionsTitleTextView.setText(suggestion.getTitle());
        holder.suggestionDescriptionTextView.setText(suggestion.getDescription());
        Picasso.get().load(suggestion.getImageUrl())
                .placeholder(R.drawable.background)
                .into(holder.suggestionImageView);
    }

    @Override
    public int getItemCount() {
        return mSuggestions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView suggestionsTitleTextView;
        private TextView suggestionDescriptionTextView;
        private ImageView suggestionImageView;
        private ImageButton saveButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            suggestionsTitleTextView = itemView.findViewById(R.id.suggestionTitle);
            suggestionDescriptionTextView = itemView.findViewById(R.id.suggestionDescription);
            suggestionImageView = itemView.findViewById(R.id.suggestionImage);
            saveButton = itemView.findViewById(R.id.saveButton);

            saveButton.setOnClickListener(v -> {
                handleStoryMenuClicked();
            });
        }

        private void handleStoryMenuClicked() {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(mContext, saveButton);
            //inflating menu from xml resource
            popup.getMenuInflater().inflate(R.menu.post_menu, popup.getMenu());
            popup.show();

            // adding click listener
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_save) {
                    saveItemToList(getAdapterPosition());
                }
                return false;
            });
        }

        private void saveItemToList(int position) {
            Suggestion suggestion = mSuggestions.get(position);
            Intent intent = new Intent(mContext, NewItemActivity.class);
            intent.putExtra("title", suggestion.getTitle());
            mContext.startActivity(intent);
        }

    }
}
