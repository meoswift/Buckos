package com.example.buckos.ui.buckets.items;

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
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;
import com.example.buckos.ui.buckets.items.itemdetails.ItemDetailsActivity;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.app.Activity.RESULT_OK;

// This fragment displays a list of incomplete items in a bucket list. User can add a new item
// from this fragment.
public class InProgressFragment extends Fragment {
    public static final int MODIFY_ITEM_REQ = 123;

    private RecyclerView mItemsRecyclerView;
    private ItemsAdapter mAdapter;
    private EditText mNewItemEditText;
    private TextView mAddItemImageView;

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
        mNewItemEditText = view.findViewById(R.id.newItemEt);
        mAddItemImageView = view.findViewById(R.id.addItemBtn);
        mItemsRecyclerView = view.findViewById(R.id.itemsRv);

        // Get the current bucket list clicked on
        Bundle bundle = this.getArguments();
        mBucketList = Parcels.unwrap(bundle.getParcelable("bucketList"));

        // Initialize list of itemss
        mItemsList = new ArrayList<>();
        // Set up the adapter for lists of incomplete items
        mAdapter = new ItemsAdapter(getContext(), mItemsList, this);
        mItemsRecyclerView.setAdapter(mAdapter);
        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        queryIncompleteItemsInList();

        mAddItemImageView.setOnClickListener(new View.OnClickListener() {
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
                mItemsList.clear();
                mItemsList.addAll(objects);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    // Function to add a new item to database based on user input
    private void addNewItem() {
        String itemTitle = mNewItemEditText.getText().toString();

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
                mItemsRecyclerView.scrollToPosition(0);
            }
        });

        mNewItemEditText.setText(null); // clear out add item edittext
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When user come back from Edit/View item screen, update the content of item
        if (resultCode == RESULT_OK && requestCode == MODIFY_ITEM_REQ) {
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
            }

            mAdapter.notifyDataSetChanged();
        }
    }
}