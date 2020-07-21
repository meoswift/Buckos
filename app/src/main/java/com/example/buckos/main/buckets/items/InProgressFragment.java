package com.example.buckos.main.buckets.items;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.main.buckets.BucketList;
import com.example.buckos.main.buckets.items.ItemsAdapter;
import com.example.buckos.main.buckets.items.content.ItemDetailsActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

// This fragment displays a list of incomplete items in a bucket list. User can add a new item
// from this fragment.
public class InProgressFragment extends Fragment {
    public static final int MODIFY_ITEM_REQ = 123;

    private RecyclerView mItemsRv;
    private ItemsAdapter mAdapter;
    private EditText mNewItemEt;
    private TextView mAddItemIv;

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
        // Set up the adapter for lists of incomplete items
        mAdapter = new ItemsAdapter(getContext(), mItemsList, this);
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

    // Get all the items in a list that are not completed yet
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

                mItemsList.clear();
                mItemsList.addAll(objects);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    // Function to add a new item to database based on user input
    private void addNewItem() {
        String itemTitle = mNewItemEt.getText().toString();

        // If title is empty, cannot create new item
        if (itemTitle.isEmpty()) {
            Toast.makeText(getContext(), "Item cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set properties of the new item
        final Item item = new Item();
        item.setName(itemTitle);
        item.setDescription("");
        item.setCompleted(false);
        item.setList(mBucketList);
        item.setAuthor(ParseUser.getCurrentUser());

        // save new item to database and update recycler view
        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mItemsList.add(0, item);
                mAdapter.notifyItemInserted(0);
                mItemsRv.scrollToPosition(0);
            }
        });

        mNewItemEt.setText(null); // clear out add item edittext
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When user come back from Edit/View item screen, update the content of item
        if (resultCode == RESULT_OK && requestCode == MODIFY_ITEM_REQ) {
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

            mAdapter.notifyDataSetChanged();
        }
    }

}