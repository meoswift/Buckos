package com.example.buckos.main.profile.bucketlists.items.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.buckos.R;
import com.example.buckos.main.profile.bucketlists.items.Item;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private List<Photo> mPhotos;
    private Context mContext;
    private Activity mActivity;

    public PhotosAdapter(List<Photo> photos, Context context, Activity activity) {
        mPhotos = photos;
        mContext = context;
        mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = mPhotos.get(position);
        ParseFile image = photo.getPhotoFile();
        if (image != null) {
            Glide.with(mContext).load(image.getUrl())
                    .transform(new RoundedCorners(25)).into(holder.photoIv);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView photoIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoIv = itemView.findViewById(R.id.photoIv);
        }
    }

    public void addNewPhoto(Item item, File photoFile) {
        final Photo photo = new Photo();
        photo.setItem(item);
        photo.setPhotoFile(new ParseFile(photoFile));

        photo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mPhotos.add(photo);
                notifyDataSetChanged();
            }
        });
    }
}
