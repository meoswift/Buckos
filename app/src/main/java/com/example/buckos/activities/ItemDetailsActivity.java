package com.example.buckos.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class ItemDetailsActivity extends AppCompatActivity {

    private EditText mItemTitleEt;
    private EditText mItemNoteEt;
    private TextView mDoneTv;
    private ScrollView mScrollView;

    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get the item object that user clicked on
        Intent intent = getIntent();
        item = Parcels.unwrap(intent.getParcelableExtra("item"));

        // Find views
        mItemTitleEt = findViewById(R.id.itemTitleEt);
        mItemNoteEt = findViewById(R.id.itemNoteEt);
        mDoneTv = findViewById(R.id.doneTv);
        mScrollView = findViewById(R.id.nestedScroll);

        mScrollView.requestDisallowInterceptTouchEvent(true);

        populateItemDetails();

        mDoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

    }

    private void saveChanges() {
        item.setName(mItemTitleEt.getText().toString());
        item.setDescription(mItemNoteEt.getText().toString());

        item.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent();
                intent.putExtra("item", Parcels.wrap(item));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int action = event.getAction();
//
//        switch (action) {
//            case (MotionEvent.ACTION_DOWN):
//                Log.d("HUH", "Action was DOWN");
//                return true;
//            case (MotionEvent.ACTION_MOVE):
//                mItemNoteEt.clearFocus();
//                mItemTitleEt.clearFocus();
//                Log.d("HUH", "Action was MOVE");
//                return true;
//            case (MotionEvent.ACTION_UP):
//                Log.d("HUH", "Action was UP");
//                return true;
//            default:
//                return super.onTouchEvent(event);
//        }
//    }

    // Populate views
    private void populateItemDetails() {
        mItemTitleEt.setText(item.getName());

        if (item.getDescription() != null)
            mItemNoteEt.setText(item.getDescription());
    }

}