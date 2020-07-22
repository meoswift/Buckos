package com.example.buckos.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.buckos.R;
import com.example.buckos.app.create.NewListActivity;
import com.example.buckos.app.explore.SearchUserFragment;
import com.example.buckos.app.feed.HomeFragment;
import com.example.buckos.app.travel.TravelFragment;
import com.example.buckos.app.buckets.BucketsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// This activity has a Bottom navigation view that allows users to navigate the app
public class MainActivity extends AppCompatActivity {
    private static final int NEW_LIST_REQUEST = 145;
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
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragment = null;
                // Switch fragments depending on chosen menu item
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_add:
                        Intent intent = new Intent(getApplicationContext(), NewListActivity.class);
                        startActivityForResult(intent, NEW_LIST_REQUEST);
                        break;
                    case R.id.action_buckets:
                        fragment = new BucketsFragment();
                        break;
                    case R.id.action_travel:
                        fragment = new TravelFragment();
                        break;
                    case R.id.action_explore:
                        fragment = new SearchUserFragment();
                    default:
                        break;
                }
                // Replace the contents of the container with the new fragment and update in view
                if (fragment != null) {
                    fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment).commit();
                }
                return true;
            }
        });
        // Set default selection
        mBottomNavigationView.setSelectedItemId(R.id.action_home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == NEW_LIST_REQUEST) {
            fragment = new BucketsFragment();
            fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment).commit();
        }
    }
}