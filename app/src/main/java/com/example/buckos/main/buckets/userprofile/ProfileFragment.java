package com.example.buckos.main.buckets.userprofile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.buckos.R;
import com.parse.ParseUser;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int EDIT_PROFILE_REQ = 111;
    private TextView mDisplayNameTv;
    private TextView mBioTv;
    private ImageView mProfilePicIv;
    private TextView mEditProfileButton;
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
        mEditProfileButton = view.findViewById(R.id.editProfileBtn);

        populateUserProfile();

        // Navigate to Edit Profile screen on button clicked
        handleEditProfile();
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

    // When user clicks Edit Profile button, takes them to Edit screen
    public void handleEditProfile() {
        mEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQ);
            }
        });
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

}