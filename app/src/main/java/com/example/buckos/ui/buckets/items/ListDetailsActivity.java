package com.example.buckos.ui.buckets.items;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

// Activity that displays the list of completed and incomplete items in a specific list
public class ListDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int EDIT_LIST_REQUEST = 345;
    private TabLayout mTabLayout;

    private Fragment mFragment;
    private Bundle mBundle;
    private BucketList mBucketList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        // Find views
        mTabLayout = findViewById(R.id.tab_layout);
        TextView listTitleTextView = findViewById(R.id.listTitleTv);
        TextView listDescriptionTextView = findViewById(R.id.listDescription);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton editListButton = findViewById(R.id.editListButton);
        TextView categoryTagTextView = findViewById(R.id.categoryTag);

        // Unwrap list object sent by previous fragment
        Intent intent = getIntent();
        mBucketList = Parcels.unwrap(intent.getParcelableExtra("bucketList"));

        mFragment = new InProgressFragment(); // Default tab is In Progress
        mBundle = new Bundle();
        mBundle.putParcelable("bucketList", Parcels.wrap(mBucketList));
        mFragment.setArguments(mBundle);

        // Populate list information
        listTitleTextView.setText(mBucketList.getShortenedTitle());
        if (!mBucketList.getDescription().equals("")) {
            listDescriptionTextView.setText(mBucketList.getDescription());
            listDescriptionTextView.setVisibility(View.VISIBLE);
        }
        categoryTagTextView.setText(mBucketList.getCategory().getCategoryName());


        backButton.setOnClickListener(this);
        editListButton.setOnClickListener(this);

        updateTabWithFragment(); // display In Progress when created

        updateTabOnSelected(); // change tabs on selection
    }

    // When using swipes between two tabs, update the according fragment
    public void updateTabOnSelected() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                // Open the fragment depending on which tab was chosen
                switch (position) {
                    case 0:
                        mFragment = new InProgressFragment();
                        mFragment.setArguments(mBundle);
                        break;
                    case 1:
                        mFragment = new DoneFragment();
                        mFragment.setArguments(mBundle);
                        break;
                }

                updateTabWithFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Function to replace layout with the Tab selected
    private void updateTabWithFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.tab_placeholder, mFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButton:
                finish();
                break;
            case R.id.editListButton:
                Intent intent = new Intent(this, EditListActivity.class);
                intent.putExtra("list", Parcels.wrap(mBucketList));
                startActivityForResult(intent, EDIT_LIST_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LIST_REQUEST && resultCode == RESULT_OK) {
            mBucketList = Parcels.unwrap(data.getParcelableExtra("list"));
            TextView listTitleTextView = findViewById(R.id.listTitleTv);
            TextView listDescriptionTextView = findViewById(R.id.listDescription);

            // populate views based on changes
            listTitleTextView.setText(mBucketList.getName());
            if (!mBucketList.getDescription().equals("")) {
                listDescriptionTextView.setText(mBucketList.getDescription());
                listDescriptionTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}