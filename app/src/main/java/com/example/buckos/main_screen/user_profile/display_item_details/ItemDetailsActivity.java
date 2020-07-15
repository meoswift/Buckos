package com.example.buckos.main_screen.user_profile.display_item_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.main_screen.user_profile.display_items_in_list.Item;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

// This class displays the details of a bucket list item - Title and Note
// User can also edit their note in this activity
public class ItemDetailsActivity extends AppCompatActivity {

    public static final String DELETE_ITEM = "deleteItem";
    public static final String EDIT_ITEM = "editItem";

    private EditText mItemTitleEt;
    private EditText mItemNoteEt;
    private ImageView mBackBtnIv;
    private TextView mShareTv;
    private BottomAppBar mBottomAppBar;
    private NestedScrollView mNestedScrollView;

    private Item item;
    private int itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get the item object that user clicked on and the position of that item in RV
        Intent intent = getIntent();
        item = Parcels.unwrap(intent.getParcelableExtra("item"));
        itemPosition = intent.getExtras().getInt("position");

        // Find views
        mItemTitleEt = findViewById(R.id.itemTitleEt);
        mItemNoteEt = findViewById(R.id.itemNoteEt);
        mBackBtnIv = findViewById(R.id.backBtn);
        mShareTv = findViewById(R.id.shareTv);
        mBottomAppBar = findViewById(R.id.bottomAppBar);
        mNestedScrollView = findViewById(R.id.nestedScroll);

        // If item clicked on is already completed, do not show option to Share
        if (!item.getCompleted())
            mShareTv.setText(null);

        // Populate title and note of an item
        populateItemDetails();

        // When user press back, save all changes and update to database
        mBackBtnIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        // When user click on a menu item in bottom app bar, perform the appropriate functionality
        handleBottomAppMenuClick();
        // When user scroll through the item, they can read or edit the item on touch
        handleScrollViewClicked();

    }

    // On each menu option, calls the right function to perform the task
    public void handleBottomAppMenuClick() {
        mBottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_delete)
                    deleteItem();
                return false;
            }
        });
    }

    // Populate views
    private void populateItemDetails() {
        mItemTitleEt.setText(item.getName());

        if (item.getDescription() != null)
            mItemNoteEt.setText(item.getDescription());
    }

    // Set item's properties with changes and save in background
    private void saveChanges() {
        item.setName(mItemTitleEt.getText().toString());
        item.setDescription(mItemNoteEt.getText().toString());

        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                intent.putExtra("item", Parcels.wrap(item));
                intent.putExtra("position", itemPosition);
                intent.putExtra("action", EDIT_ITEM);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // Remove item from database and update RecyclerView
    private void deleteItem() {
        item.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                intent.putExtra("position", itemPosition);
                intent.putExtra("action", DELETE_ITEM);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    // When scroll view is touched, determine which one is a tap, and which is a scroll
    @SuppressLint("ClickableViewAccessibility")
    public void handleScrollViewClicked() {
        mNestedScrollView.setOnTouchListener(new View.OnTouchListener() {
            private long startClickTime;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // starts timing when user click down on screen
                    startClickTime = System.currentTimeMillis();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    // keyboard manager
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    // Touch was a simple tap - focus on edit text and show keyboard
                    if (System.currentTimeMillis() - startClickTime < ViewConfiguration.getTapTimeout()) {
                        mItemNoteEt.requestFocus();
                        imm.showSoftInput(mItemNoteEt, 0);

                    // Touch was scrolling - clear focus and hide keyboard
                    } else {
                        mItemTitleEt.clearFocus();
                        mItemNoteEt.clearFocus();
                        imm.hideSoftInputFromWindow(mNestedScrollView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }

}