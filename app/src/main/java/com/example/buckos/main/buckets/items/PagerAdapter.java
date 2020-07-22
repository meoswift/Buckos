package com.example.buckos.main.buckets.items;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.buckos.main.buckets.BucketList;

import org.parceler.Parcels;

// This adapter updates Fragment when user swipe between Incomplete and Done tabs in List Details.
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private BucketList mBucketList;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, BucketList bucketList) {
        super(fm);
        mNumOfTabs = NumOfTabs;
        mBucketList = bucketList;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putParcelable("bucketList", Parcels.wrap(mBucketList));

        // Open the fragment depending on which tab was chosen
        switch (position) {
            case 0:
                fragment = new InProgressFragment();
                fragment.setArguments(bundle);
                return fragment;
            case 1:
                fragment = new DoneFragment();
                fragment.setArguments(bundle);
                return fragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}