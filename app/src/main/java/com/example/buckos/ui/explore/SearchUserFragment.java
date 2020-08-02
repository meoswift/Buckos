package com.example.buckos.ui.explore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.buckos.R;
import com.example.buckos.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

// Fragment that allows user to search for others with their username. A list of results will be
// displayed depending on query.
public class SearchUserFragment extends Fragment {

    private EditText mUsernameQuery;
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
        RecyclerView userResultsRecyclerView = view.findViewById(R.id.userResultsRv);
        TextView suggestionsTextView = view.findViewById(R.id.suggestionsTextView);
        ImageButton backButton = view.findViewById(R.id.backButton);

        mUsernameQuery.requestFocus(); // hints user

        // Set up adapter and layout manager for lists of user results
        mUsersList = new ArrayList<>();
        mAdapter = new UsersAdapter(mUsersList, getContext(), this);
        userResultsRecyclerView.setAdapter(mAdapter);
        userResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get results for query
        getUsersResults();

        // shows users following suggestions
        suggestionsTextView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DiscoverActivity.class);
            startActivity(intent);
        });

        // returns to previous fragment on back pressed
        backButton.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }

    private void getUsersResults() {
        mUsernameQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch();
            }
        });

        mUsernameQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager)
                        getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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




}