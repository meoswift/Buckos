package com.example.buckos.ui.buckets.items;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.example.buckos.ui.buckets.items.itemdetails.ItemDetailsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.app.Activity.RESULT_OK;

// This fragment displays a list of bucket list items that have been completed by user
public class DoneFragment extends Fragment {

    private RecyclerView mItemsRecyclerView;
    private ItemsAdapter mAdapter;
    private ConstraintLayout mInstructionsLayout;

    private List<Item> mItemsList;
    private BucketList mBucketList;

    public DoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_done, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mInstructionsLayout = view.findViewById(R.id.instructionsLayout);

        // get the bucket
        Bundle bundle = this.getArguments();
        mBucketList = Parcels.unwrap(bundle.getParcelable("bucketList"));

        // set up adapter
        mItemsList = new ArrayList<>();
        mItemsRecyclerView = view.findViewById(R.id.itemsRv);
        mAdapter = new ItemsAdapter(getContext(), mItemsList, this);
        mItemsRecyclerView.setAdapter(mAdapter);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryDoneItemsInList();
    }

    private void queryDoneItemsInList() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        // get all items in the current bucket list that are completed
        query.whereEqualTo(Item.KEY_LIST, mBucketList);
        query.whereEqualTo(Item.KEY_COMPLETED, true);
        // order items by descending time created
        query.orderByDescending(Item.KEY_TIME_COMPLETED);
        // start async function to get Item objects
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                mItemsList.clear();
                mItemsList.addAll(objects);
                mAdapter.notifyDataSetChanged();

                if (mItemsList.size() == 0) {
                    mInstructionsLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When user come back from Edit/View item screen, update the content of item
        if (resultCode == RESULT_OK && requestCode == InProgressFragment.MODIFY_ITEM_REQ) {
            Item item = Parcels.unwrap(data.getParcelableExtra(ItemsAdapter.KEY_ITEM));
            String action = data.getStringExtra("action");
            int position = data.getExtras().getInt(ItemsAdapter.KEY_POSITION);

            // Update RecyclerView based on whether user edited or deleted the item
            switch (action) {
                case ItemDetailsActivity.EDIT_ITEM:
                    mItemsList.set(position, item);
                    break;
                case ItemDetailsActivity.DELETE_ITEM:
                    mItemsList.remove(position);
                    Snackbar.make(this.getView(), R.string.item_delete, Snackbar.LENGTH_SHORT).show();
                    break;
                case ItemDetailsActivity.POST_ITEM:
                    Intent intent = new Intent();

                    // finish ListDetailsActivity and back to BucketsFragment
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
            }

            mAdapter.notifyDataSetChanged();
        }
    }

}