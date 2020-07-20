package com.example.buckos.main.explore;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.main.buckets.userprofile.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class SearchUserFragment extends Fragment {

    private EditText mUsernameQuery;
    private RecyclerView mUserResultsRv;
    private List<User> mUsersList;
    private UsersAdapter mAdapter;

    public SearchUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        mUsernameQuery = view.findViewById(R.id.usernameInputEt);
        mUserResultsRv = view.findViewById(R.id.userResultsRv);

        // Set up adapter and layout manager for lists of user results
        mUsersList = new ArrayList<>();
        mAdapter = new UsersAdapter(mUsersList, getContext());
        mUserResultsRv.setAdapter(mAdapter);
        mUserResultsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        getUsersResults();
    }

    private void getUsersResults() {
        mUsernameQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(mUsernameQuery.getWindowToken(), 0);
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereMatches("username", mUsernameQuery.getText().toString());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                mUsersList.clear();

                for (int i = 0; i < objects.size(); i++) {
                    User user = (User) objects.get(i);
                    mUsersList.add(user);
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", mUsernameQuery.getText().toString());
        outState.putParcelable("results", Parcels.wrap(mUsersList));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("debug", "created");

        if (savedInstanceState != null) {
            Log.d("debug", "not null");
            mUsernameQuery.setText(savedInstanceState.getString("query"));
            mUsersList = Parcels.unwrap(savedInstanceState.getParcelable("results"));
            mAdapter.notifyDataSetChanged();
        }
    }
}