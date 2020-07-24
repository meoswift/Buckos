package com.example.buckos.ui.buckets.items;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.google.android.material.tabs.TabLayout;

import org.parceler.Parcels;

// Activity that displays the list of completed and incomplete items in a specific list
public class ListDetailsActivity extends AppCompatActivity {

    private TextView mListTitleTextView;
    private TabLayout mTabLayout;
    private ImageView mBackButtonImageView;
    private TextView mListDescriptionTextView;
    private BucketList mBucketList;
    private Fragment mFragment;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        // Find views
        mListTitleTextView = findViewById(R.id.listTitleTv);
        mTabLayout = findViewById(R.id.tab_layout);
        mBackButtonImageView = findViewById(R.id.backButton);
        mListDescriptionTextView = findViewById(R.id.listDescription);

        // Unwrap list object sent by previous fragment
        Intent intent = getIntent();
        mBucketList = Parcels.unwrap(intent.getParcelableExtra("bucketList"));

        mFragment = new InProgressFragment(); // Default tab is In Progress
        mBundle = new Bundle();
        mBundle.putParcelable("bucketList", Parcels.wrap(mBucketList));
        mFragment.setArguments(mBundle);

        // Update list title in tool bar
        mListTitleTextView.setText(mBucketList.getName());
        if (!mBucketList.getDescription().equals("")) {
            mListDescriptionTextView.setText(mBucketList.getDescription());
            mListDescriptionTextView.setVisibility(View.VISIBLE);
        }

        handleBackPress(); // on back button clicked

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


    // When user click back button on screen, takes them to previous screen
    public void handleBackPress() {
        mBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}