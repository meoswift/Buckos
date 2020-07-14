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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.adapters.ListItemsAdapter;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class InProgressFragment extends Fragment {

    private RecyclerView mItemsRv;
    private ListItemsAdapter mAdapter;
    private EditText mNewItemEt;
    private ImageView mAddItemIv;

    private List<Item> mItemsList;
    private BucketList mBucketList;

    public InProgressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_in_progress, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        mNewItemEt = view.findViewById(R.id.newItemEt);
        mAddItemIv = view.findViewById(R.id.addItemBtn);
        mItemsRv = view.findViewById(R.id.itemsRv);

        // Get the current bucket list clicked on
        Bundle bundle = this.getArguments();
        mBucketList = Parcels.unwrap(bundle.getParcelable("bucketList"));

        // Initialize list of itemss
        mItemsList = new ArrayList<>();
        // Create an adapter for the list of items
        mAdapter = new ListItemsAdapter(getContext(), mItemsList);
        // Set adapter and linear layout for RecyclerView
        mItemsRv.setAdapter(mAdapter);
        mItemsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        queryIncompleteItemsInList();

        mAddItemIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
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

    private void addNewItem() {
        String itemTitle = mNewItemEt.getText().toString();
        if (itemTitle.isEmpty())
            Toast.makeText(getContext(), "Item cannot be empty!", Toast.LENGTH_SHORT).show();

        final Item item = new Item();
        item.setName(itemTitle);
        item.setCompleted(false);
        item.setList(mBucketList);
        item.setAuthor(ParseUser.getCurrentUser());

        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mItemsList.add(item);
                mAdapter.notifyDataSetChanged();
            }
        });

        mNewItemEt.setText(null);
    }


}