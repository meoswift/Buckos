package com.example.buckos.ui.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.Comment;

import java.util.List;

// This class implements an adapter. For each Comment object, the adapter will create a
// ViewHolder and bind that ViewHolder to the RecyclerView.
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    List<Comment> mComments;
    Context mContext;

    public CommentsAdapter(List<Comment> comments, Context context) {
        mComments = comments;
        mContext = context;
    }

    public CommentsAdapter(List<org.w3c.dom.Comment> mCommentsList, CommentsActivity context) {
    }

    @NonNull
    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.ViewHolder holder, int position) {
        Comment comment = mComments.get(position);

        holder.nameTextView.setText(comment.getUser().getName());
        holder.commentTextView.setText(comment.getBody());
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView commentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find views
            nameTextView = itemView.findViewById(R.id.nameTv);
            commentTextView = itemView.findViewById(R.id.commentBodyTv);
        }
    }
}