package com.example.buckos.main.explore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.User;
import com.example.buckos.main.travel.PlacesAdapter;

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
    }

    @Override
    public int getItemCount() {
        return mUsersResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView profilePicIv;
        private TextView usernameTv;
        private TextView displayNameTv;
        private 

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicIv = itemView.findViewById(R.id.profilePicIv);
            usernameTv = itemView.findViewById(R.id.usernameTv);
            displayNameTv = itemView.findViewById(R.id.displayNameTv);
        }
    }
}
