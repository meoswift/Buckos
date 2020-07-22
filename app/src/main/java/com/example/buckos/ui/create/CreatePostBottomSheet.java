package com.example.buckos.ui.create;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.buckos.R;
import com.example.buckos.ui.MainActivity;
import com.example.buckos.ui.buckets.BucketsFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static android.app.Activity.RESULT_OK;

public class CreatePostBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final int NEW_ITEM_REQUEST = 1011;
    private static final int NEW_LIST_REQUEST = 1000;

    private TextView mBucketTextView;
    private TextView mItemTextView;
    private TextView mStoryTextView;

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

        mBucketTextView = view.findViewById(R.id.bucketTextView);
        mItemTextView = view.findViewById(R.id.itemTextView);
        mStoryTextView = view.findViewById(R.id.storyTextView);

        mBucketTextView.setOnClickListener(this);
        mItemTextView.setOnClickListener(this);
        mStoryTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bucketTextView:
                Intent intentNewList = new Intent(getContext(), NewListActivity.class);
                startActivityForResult(intentNewList, MainActivity.NEW_LIST_REQUEST);
                break;
            case R.id.itemTextView:
                Intent intentNewItem = new Intent(getContext(), NewItemActivity.class);
                startActivityForResult(intentNewItem, NEW_ITEM_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_LIST_REQUEST && resultCode == RESULT_OK) {
            Fragment fragment = new BucketsFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.your_placeholder, fragment).commit();
            this.dismiss();
        }

        if (requestCode == NEW_ITEM_REQUEST && resultCode == RESULT_OK) {
            this.dismiss();
        }

    }
}
