package com.example.buckos.main.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.main.buckets.userprofile.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<User> mUsersResults;
    private Context mContext;

    public UsersAdapter(List<User> usersResults, Context context) {
        this.mUsersResults = usersResults;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View place = inflater.inflate(R.layout.user_item, parent, false);
        return new ViewHolder(place);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsersResults.get(position);
        holder.usernameTv.setText(user.getUsername());
        holder.displayNameTv.setText(user.getName());
        Glide.with(mContext).load(mContext.getDrawable(R.drawable.background)).circleCrop().into(holder.profilePicIv);
    }

    @Override
    public int getItemCount() {
        return mUsersResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView profilePicIv;
        private TextView usernameTv;
        private TextView displayNameTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicIv = itemView.findViewById(R.id.profilePicIv);
            usernameTv = itemView.findViewById(R.id.usernameTv);
            displayNameTv = itemView.findViewById(R.id.displayNameTv);
        }
    }
}
