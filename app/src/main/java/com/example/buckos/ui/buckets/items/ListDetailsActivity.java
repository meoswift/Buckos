package com.example.buckos.ui.buckets.items;

import android.content.Intent;
import android.os.Bundle;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

// Activity that displays the list of completed and incomplete items in a specific list
public class ListDetailsActivity extends AppCompatActivity {

    private TextView mListTitleTextView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private ImageView mBackButtonImageView;
    private TextView mListDescriptionTextView;
    private BucketList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        // Find views
        mListTitleTextView = findViewById(R.id.listTitleTv);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.pager);
        mBackButtonImageView = findViewById(R.id.backButton);
        mListDescriptionTextView = findViewById(R.id.listDescription);

        // Unwrap list object sent by previous fragment
        Intent intent = getIntent();
        list = Parcels.unwrap(intent.getParcelableExtra("bucketList"));

        // Update list title in tool bar
        mListTitleTextView.setText(list.getName());
        if (!list.getDescription().equals("")) {
            mListDescriptionTextView.setText(list.getDescription());
            mListDescriptionTextView.setVisibility(View.VISIBLE);
        }

        handleBackPress(); // on back button clicked
        changeTabOnSwiping(); // on swiping, change tabs
    }

    // When using swipes between two tabs, update the according fragment
    public void changeTabOnSwiping() {
        // Set adapter to change fragment when user swipe between two tabs
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), list);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // When user click back button on screen, takes them to previous screen
    public void handleBackPress() {
        mBackButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDetailsActivity.super.onBackPressed();
            }
        });
    }

}