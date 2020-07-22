package com.example.buckos.app.travel;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.buckos.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

// Fragment that displays a list of tourist attractions based on user's query.
// User can bookmark a place and add that place to multiple lists.
public class TravelFragment extends Fragment {

    public static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    private EditText mCityQueryEt;
    private RecyclerView mCityResultsRv;
    private ImageView mTravelArt;
    private ProgressBar mProgressBar;

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
        mTravelArt = view.findViewById(R.id.travelArt);
        mProgressBar = view.findViewById(R.id.travelProgressBar);

        mCityQueryEt.requestFocus();

        // Set up the adapter that will display results - POIs based on of user query
        mPlaces = new ArrayList<>();
        mAdapter = new PlacesAdapter(mPlaces, getContext());
        mCityResultsRv.setAdapter(mAdapter);
        mCityResultsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        getSearchCityResults();
    }

    // Perform search when user click on Search button in keyboard
    public void getSearchCityResults() {
        mCityQueryEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(mCityQueryEt.getWindowToken(), 0);
                    mProgressBar.setVisibility(View.VISIBLE);
                    performSearch();
                    mTravelArt.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
    }

    // Retrieve JSON results from Places API, parse into Place list, and notify adapter.
    private void performSearch() {
        // Format query into appropriate URI format "+"
        String query = mCityQueryEt.getText().toString();
        String formattedQuery = "point+of+interest+in+" + query.replace(" ", "+");

        // Makes API call to get a list of places
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String api_key = getContext().getString(R.string.google_maps_api_key);
        params.put("query", formattedQuery);
        params.put("key", api_key);

        client.get(PLACES_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    // Parse results array into list of Place objects
                    mPlaces.clear();
                    mPlaces.addAll(Place.jsonToList(results));
                    mProgressBar.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Toast.makeText(getContext(), "Failure to get list of attractions.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}