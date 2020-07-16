package com.example.buckos.main_screen.user_profile.display_item_details.display_done_items;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.buckos.R;
import com.example.buckos.main_screen.user_profile.display_bucket_lists.BucketList;
import com.example.buckos.main_screen.user_profile.display_item_details.display_incomplete_items.InProgressFragment;
import com.example.buckos.main_screen.user_profile.display_item_details.Item;
import com.example.buckos.main_screen.user_profile.display_item_details.ItemsAdapter;
import com.example.buckos.main_screen.user_profile.item_details_screen.ItemDetailsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// This fragment displays a list of bucket list items that have been completed by user
public class DoneFragment extends Fragment {

    private RecyclerView mItemsRv;
    private ItemsAdapter mAdapter;

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

        Bundle bundle = this.getArguments();
        mBucketList = Parcels.unwrap(bundle.getParcelable("bucketList"));

        mItemsList = new ArrayList<>();
        mItemsRv = view.findViewById(R.id.itemsRv);
        mAdapter = new ItemsAdapter(getContext(), mItemsList, this);
        mItemsRv.setAdapter(mAdapter);
        mItemsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter.notifyDataSetChanged();
        queryDoneItemsInList();
    }

    private void queryDoneItemsInList() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        // get all items in the current bucket list
        query.whereEqualTo(Item.KEY_LIST, mBucketList);
        // get all items that are not completed
        query.whereEqualTo(Item.KEY_COMPLETED, true);
        // order items by descending time created
        query.orderByDescending(Item.KEY_CREATED_AT);
        // start async function to get Item objects
        query.findInBackground(new FindCallback<Item>() {
            @Override
            public void done(List<Item> objects, ParseException e) {
                if (e != null) {
                    Log.d("InProgressFragment", e.toString());
                    return;
                }

                mItemsList.clear();
                mItemsList.addAll(objects);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When user come back from Edit/View item screen, update the content of item
        if (resultCode == RESULT_OK && requestCode == InProgressFragment.EDIT_ITEM_REQ) {
            Item item = Parcels.unwrap(data.getParcelableExtra("item"));
            String action = data.getStringExtra("action");
            int position = data.getExtras().getInt("position");

            // Update RecyclerView based on whether user edited or deleted the item
            switch (action) {
                case ItemDetailsActivity.EDIT_ITEM:
                    mItemsList.set(position, item);
                    break;
                case ItemDetailsActivity.DELETE_ITEM:
                    mItemsList.remove(position);
                    break;
            }
        }
    }

}