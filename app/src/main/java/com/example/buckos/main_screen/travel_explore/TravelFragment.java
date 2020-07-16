package com.example.buckos.main_screen.travel_explore;

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

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.buckos.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TravelFragment extends Fragment {

    public static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    private EditText mCityQueryEt;
    private RecyclerView mCityResultsRv;

    private List<Place> mPlaces;
    private PlacesAdapter mAdapter;

    public TravelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_travel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find views
        mCityQueryEt = view.findViewById(R.id.cityInputEt);
        mCityResultsRv = view.findViewById(R.id.cityResultsRv);

        mPlaces = new ArrayList<>();
        mAdapter = new PlacesAdapter(mPlaces, getContext());
        mCityResultsRv.setAdapter(mAdapter);
        mCityResultsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        getSearchCityResults();
    }

    public void getSearchCityResults() {
        mCityQueryEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(mCityQueryEt.getWindowToken(), 0);
                    mCityQueryEt.clearFocus();
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {
        String query = mCityQueryEt.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("query", "point+of+interest+in" + query);
        params.put("key", "AIzaSyAuxd6TMwxnqTCAfFEMSKG1vcPjemiUb-0");

        client.get(PLACES_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    mPlaces.addAll(Place.jsonToList(results));
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }
}