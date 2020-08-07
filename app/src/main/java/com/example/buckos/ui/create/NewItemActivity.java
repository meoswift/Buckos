package com.example.buckos.ui.create;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.Item;
import com.example.buckos.models.Story;
import com.example.buckos.ui.travel.placebookmark.SaveToListActivity;
import com.parse.ParseUser;

import org.parceler.Parcels;

public class NewItemActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int NEW_ITEM_REQUEST = 1010;
    private EditText mItemTitleEditText;
    private EditText mItemDescriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        mItemTitleEditText = findViewById(R.id.itemTitleEt);
        mItemDescriptionEditText = findViewById(R.id.itemDescriptionEt);
        TextView mCreateButton = findViewById(R.id.createBtn);
        ImageView mCloseButton = findViewById(R.id.closeBtn);

        // Show cursor at description to hint user
        mItemDescriptionEditText.requestFocus();

        // coming from home feed or explore tab with an item
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if (title != null) {
            mItemTitleEditText.setText(title);
        }

        mCreateButton.setOnClickListener(this);
        mCloseButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createBtn:
                createNewItem();
                break;
            case R.id.closeBtn:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
        }
    }

    private void createNewItem() {
        Item item = new Item();
        item.setAuthor(ParseUser.getCurrentUser());
        item.setName(mItemTitleEditText.getText().toString());
        item.setDescription(mItemDescriptionEditText.getText().toString());

        Intent intent = new Intent(this, SaveToListActivity.class);
        intent.putExtra("item", Parcels.wrap(item));
        startActivityForResult(intent, NEW_ITEM_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}