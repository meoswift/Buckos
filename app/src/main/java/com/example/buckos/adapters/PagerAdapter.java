package com.example.buckos.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.buckos.fragments.DoneFragment;
import com.example.buckos.fragments.InProgressFragment;
import com.example.buckos.models.BucketList;
import com.example.buckos.models.Item;

import org.parceler.Parcels;

import java.util.List;

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