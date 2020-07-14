package com.example.buckos.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.buckos.R;
import com.example.buckos.adapters.PagerAdapter;
import com.example.buckos.models.BucketList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.TextView;

import org.parceler.Parcels;

public class ListDetailsActivity extends AppCompatActivity {

    private TextView mListTitleTv;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        // Find views
        mListTitleTv = findViewById(R.id.listTitleTv);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.pager);

        // Unwrap list object sent by previous fragment
        Intent intent = getIntent();
        BucketList list = Parcels.unwrap(intent.getParcelableExtra("bucketList"));

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), list);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setAdapter(mPagerAdapter);

        mListTitleTv.setText(list.getName());
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}