package com.example.buckos.main.buckets.userprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.example.buckos.authentication.LoginActivity;
import com.parse.ParseUser;

import static android.app.Activity.RESULT_OK;

// Fragment to display current user's information: display name, bio, and posts. User can log out
// or navigate to Edit profile screen from this fragment.
public class ProfileFragment extends Fragment {

    private static final int EDIT_PROFILE_REQ = 111;
    private TextView mDisplayNameTv;
    private TextView mBioTv;
    private ImageView mProfilePicIv;
    private Toolbar mProfileToolbar;
    private ImageView mBackButton;
    private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user
        user = (User) ParseUser.getCurrentUser();

        // Find views
        mDisplayNameTv = view.findViewById(R.id.displayNameTv);
        mBioTv = view.findViewById(R.id.bioTv);
        mProfilePicIv = view.findViewById(R.id.profilePicIv);
        mProfileToolbar = view.findViewById(R.id.profileToolbar);
        mBackButton = view.findViewById(R.id.backButton);

        populateUserProfile();
        handleProfileMenuClicked();
        handleBackButtonClicked();
    }

    private void handleBackButtonClicked() {
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void handleProfileMenuClicked() {
        mProfileToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_edit) {
                    Intent intent = new Intent(getContext(), EditProfileActivity.class);
                    startActivityForResult(intent, EDIT_PROFILE_REQ);
                } else {
                    ParseUser.logOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                return false;
            }
        });

    }

    // Populate views that comes with an user profile
    private void populateUserProfile() {
        mDisplayNameTv.setText(user.getName());
        mBioTv.setText(user.getBio());
        Glide.with(getContext()).load(getContext()
                .getDrawable(R.drawable.bucket))
                .circleCrop()
                .into(mProfilePicIv);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Display the updated information of user profile after changes
        if (resultCode == RESULT_OK && requestCode == EDIT_PROFILE_REQ) {
            User user = (User) ParseUser.getCurrentUser();
            mDisplayNameTv.setText(user.getName());
            mBioTv.setText(user.getBio());
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQ);
        } else {
            ParseUser.logOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // prevents user from going back
        }

        return true;
    }


}