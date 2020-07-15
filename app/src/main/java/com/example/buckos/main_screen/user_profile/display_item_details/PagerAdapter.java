package com.example.buckos.main_screen.user_profile.display_item_details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.buckos.main_screen.user_profile.display_bucket_lists.BucketList;
import com.example.buckos.main_screen.user_profile.display_item_details.display_done_items.DoneFragment;
import com.example.buckos.main_screen.user_profile.display_item_details.display_incomplete_items.InProgressFragment;

import org.parceler.Parcels;

// This adapter updates Fragment when user swipe between Incomplete and Done tabs in List Details.
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    BucketList mBucketList;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, BucketList mBucketList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.mBucketList = mBucketList;
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