package com.example.buckos.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.buckos.R;
import com.example.buckos.ui.create.CreatePostBottomSheet;
import com.example.buckos.ui.explore.ExploreFragment;
import com.example.buckos.ui.feed.HomeFragment;
import com.example.buckos.ui.travel.TravelExploreFragment;
import com.example.buckos.ui.buckets.BucketsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// This activity has a Bottom navigation view that allows users to navigate the app
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView mBottomNavigationView;
    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views and define fragments
        mBottomNavigationView = findViewById(R.id.bottomNavigation);
        // Define a fragment manager for bottom navigation
        fragmentManager = getSupportFragmentManager();

        // Navigate user to the correct tab when they choose a menu item
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            fragment = null;
            // Switch fragments depending on chosen menu item
            switch (menuItem.getItemId()) {
                case R.id.action_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.action_add:
                    CreatePostBottomSheet createPostBottomSheet = CreatePostBottomSheet.newInstance();
                    createPostBottomSheet.show(getSupportFragmentManager(), "bottom_sheet");
                    break;
                case R.id.action_buckets:
                    fragment = new BucketsFragment();
                    break;
                case R.id.action_travel:
                    fragment = new TravelExploreFragment();
                    break;
                case R.id.action_explore:
                    fragment = new ExploreFragment();
                default:
                    break;
            }
            // Replace the contents of the container with the new fragment and update in view
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .replace(R.id.your_placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        });
        // Set default selection
        mBottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}