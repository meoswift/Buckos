package com.example.buckos.fragments;

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
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.adapters.ListItemsAdapter;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class InProgressFragment extends Fragment {

    private RecyclerView mItemsRv;
    private ListItemsAdapter mAdapter;

    private List<Item> mItemsList;
    private BucketList mBucketList;

    public InProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();
        mBucketList = Parcels.unwrap(bundle.getParcelable("bucketList"));

        mItemsList = new ArrayList<>();
        mItemsRv = view.findViewById(R.id.itemsRv);
        mAdapter = new ListItemsAdapter(getContext(), mItemsList);
        mItemsRv.setAdapter(mAdapter);
        mItemsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        queryIncompleteItemsInList();
    }

    private void queryIncompleteItemsInList() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        // get all items in the current bucket list
        query.whereEqualTo(Item.KEY_LIST, mBucketList);
        // get all items that are not completed
        query.whereEqualTo(Item.KEY_COMPLETED, false);
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

                mItemsList.addAll(objects);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}