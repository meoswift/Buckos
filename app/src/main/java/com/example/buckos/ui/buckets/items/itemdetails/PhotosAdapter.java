package com.example.buckos.ui.buckets.items.itemdetails;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.example.buckos.models.Photo;
import com.example.buckos.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private List<Photo> mPhotos;
    private Context mContext;

    public PhotosAdapter() { }

    public PhotosAdapter(List<Photo> photos, Context context) {
        mPhotos = photos;
        mContext = context;
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
            Picasso.get().load(image.getUrl())
                    .placeholder(R.drawable.background)
                    .into(holder.photoImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView photoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoIv);

            // remove a photo on long click
            itemView.setOnLongClickListener(v -> {
                User currentUser = (User) ParseUser.getCurrentUser();
                Photo photo = mPhotos.get(getAdapterPosition());
                User user = photo.getAuthor();
                if (user.equals(currentUser)) {
                    confirmDeleteDialog(photo);
                }
                return false;
            });
        }

        private void deletePhoto(Photo photo) {
            photo.deleteInBackground(e -> {
                mPhotos.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });

        }

        private void confirmDeleteDialog(Photo photo) {
            new MaterialAlertDialogBuilder(mContext, R.style.CustomFont)
                    .setTitle("Delete photo?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        deletePhoto(photo);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // nothing
                    })
                    .show();
        }
    }

    public void addNewPhoto(Item item, ParseFile photoFile) {
        final Photo photo = new Photo();
        User currentUser = (User) ParseUser.getCurrentUser();

        // set properties - item, file, and list
        photo.setItem(item);
        photo.setPhotoFile(photoFile);
        photo.setList(item.getList());
        photo.setAuthor(currentUser);

        photo.saveInBackground(e -> {
            mPhotos.add(photo);
            notifyDataSetChanged();
        });
    }

    // query and display all photos in current items
    public void displayPhotosInCurrentItem(Item item) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Photo.KEY_ITEM, item);
        query.include(Photo.KEY_AUTHOR);
        query.findInBackground((objects, e) -> {
            mPhotos.addAll(objects);
            notifyDataSetChanged();
        });
    }

    // delete all photos added to an item
    public void deleteAllPhotosInItem(Item item) {
        ParseQuery<Photo> query = ParseQuery.getQuery(Photo.class);
        query.whereEqualTo(Photo.KEY_ITEM, item);
        query.findInBackground(new FindCallback<Photo>() {
            @Override
            public void done(List<Photo> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    Photo photo = objects.get(i);
                    photo.deleteInBackground();
                }
            }
        });
    }



}
