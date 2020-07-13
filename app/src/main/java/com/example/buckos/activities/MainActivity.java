package com.example.buckos.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.buckos.R;
import com.example.buckos.fragments.AddListFragment;
import com.example.buckos.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views and define fragments
        mBottomNavigationView = findViewById(R.id.bottomNavigation);
        // Define a fragment manager for bottom navigation
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Navigate user to the correct tab when they choose a menu item
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;
                // Switch fragments depending on chosen menu item
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_add:
                        fragment = new AddListFragment();
                        break;
                    case R.id.action_profile:
                        //
                        break;
                    default:
                        break;
                }
                // Replace the contents of the container with the new fragment and update in view
                fragmentManager.beginTransaction().replace(R.id.your_placeholder, fragment).commit();
                return true;
            }
        });
        // Set default selection
        mBottomNavigationView.setSelectedItemId(R.id.action_home);
    }
}