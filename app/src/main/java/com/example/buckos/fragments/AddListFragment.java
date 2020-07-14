package com.example.buckos.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buckos.R;
import com.example.buckos.models.BucketList;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class AddListFragment extends Fragment {

    private EditText mListTitleEt;
    private EditText mListDescriptionEt;
    private Button mCreateButton;

    public AddListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListTitleEt = view.findViewById(R.id.listTitleEt);
        mListDescriptionEt = view.findViewById(R.id.listDescriptionEt);
        mCreateButton = view.findViewById(R.id.createBtn);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listTitle = mListTitleEt.getText().toString();

                if (listTitle.isEmpty()) {
                    Toast.makeText(getContext(), "Title cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                createList();
            }
        });
    }

    private void createList() {
        BucketList list = new BucketList();

        // Set core properties of a bucket list object
        list.setName(mListTitleEt.getText().toString());
        list.setDescription(mListDescriptionEt.getText().toString());
        list.setAuthor(ParseUser.getCurrentUser());

        list.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Toast.makeText(getContext(), "Issue creating bucket list!", Toast.LENGTH_SHORT).show();

                // Take user to User Profile with appended new list
                Fragment fragment = new UserProfileFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.your_placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
}