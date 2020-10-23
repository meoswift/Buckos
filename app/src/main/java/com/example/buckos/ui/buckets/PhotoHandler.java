package com.example.buckos.ui.buckets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

// Handles adding photos from camera or gallery
public class PhotoHandler {
    public final String APP_TAG = "Buckos";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    public static final String FILENAME = "photo.png";
    public static final String AUTHORITY = "com.codepath.fileprovider.buckos";

    private File photoFile;
    private Context mContext;
    private Activity mActivity;

    public PhotoHandler(Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
    }

    /** Helper functions for uploading image files from Camera/Gallery to Parse **/

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String photoFileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "Failed to create!");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);

        return file;
    }

    // Get bytes array from URI image upload
    /* https://stackoverflow.com/questions/10296734/image-uri-to-bytesarray */
    public static byte[] getBytes(InputStream iStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while (true) {
            try {
                if ((len = iStream.read(buffer)) == -1)
                    break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
