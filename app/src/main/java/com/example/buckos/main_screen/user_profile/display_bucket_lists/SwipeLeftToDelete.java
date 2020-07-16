package com.example.buckos.main_screen.user_profile.display_bucket_lists;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.main_screen.MainActivity;
import com.example.buckos.main_screen.user_profile.UserProfileFragment;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import java.util.List;

public class SwipeLeftToDelete extends ItemTouchHelper.SimpleCallback {

    private BucketListsAdapter mAdapter;
    private List<BucketList> mLists;

    public SwipeLeftToDelete(BucketListsAdapter adapter, List<BucketList> lists) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = adapter;
        this.mLists = lists;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Remove swiped item from list and notify the RecyclerView
        final int position = viewHolder.getAdapterPosition();
        mAdapter.deleteList(position);
    }

}
