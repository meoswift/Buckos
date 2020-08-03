package com.example.buckos.ui.create;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.buckos.R;
import com.example.buckos.models.Story;
import com.example.buckos.ui.MainActivity;
import com.example.buckos.ui.buckets.BucketsFragment;
import com.example.buckos.ui.feed.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import org.parceler.Parcels;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CreatePostBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final int NEW_ITEM_REQUEST = 1011;
    private static final int NEW_LIST_REQUEST = 1000;
    private static final int NEW_STORY_REQUEST = 1555;

    private LinearLayout mBucketLinearLayout;
    private LinearLayout mItemLinearLayout;
    private LinearLayout mStoryLinearLayout;

    public static CreatePostBottomSheet newInstance() {
        return new CreatePostBottomSheet();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBucketLinearLayout = view.findViewById(R.id.bucketLinearLayout);
        mItemLinearLayout = view.findViewById(R.id.itemLinearLayout);
        mStoryLinearLayout = view.findViewById(R.id.storyLinearLayout);

        mBucketLinearLayout.setOnClickListener(this);
        mItemLinearLayout.setOnClickListener(this);
        mStoryLinearLayout.setOnClickListener(this);
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.storyLinearLayout:
                Intent newStoryIntent = new Intent(getContext(), NewStoryActivity.class);
                startActivityForResult(newStoryIntent, NEW_STORY_REQUEST);
                break;
            case R.id.bucketLinearLayout:
                Intent newListIntent = new Intent(getContext(), NewListActivity.class);
                startActivityForResult(newListIntent, NEW_LIST_REQUEST);
                break;
            case R.id.itemLinearLayout:
                Intent newItemIntent = new Intent(getContext(), NewItemActivity.class);
                startActivityForResult(newItemIntent, NEW_ITEM_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.dismiss();

        // Create new list
        if (requestCode == NEW_LIST_REQUEST) {
            if (resultCode == RESULT_OK) {
                Fragment fragment = new BucketsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).commit();
            }
        }

        // Create new item
        if (requestCode == NEW_ITEM_REQUEST) { }

        // Create new story
        if (requestCode == NEW_STORY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Fragment fragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).commit();
            }
        }

    }
}
