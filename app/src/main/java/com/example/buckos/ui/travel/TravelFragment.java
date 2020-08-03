package com.example.buckos.ui.travel;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.buckos.R;
import com.example.buckos.models.Place;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

import static android.telephony.MbmsDownloadSession.RESULT_CANCELLED;

// Fragment that displays a list of tourist attractions based on user's query.
// User can bookmark a place and add that place to multiple lists.
public class TravelFragment extends Fragment {

    public static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    private EditText mCityQueryEditText;
    private RecyclerView mCityResultsRecyclerView;
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
        mCityQueryEditText = view.findViewById(R.id.cityInputEt);
        mCityResultsRecyclerView = view.findViewById(R.id.cityResultsRv);
        mTravelArt = view.findViewById(R.id.travelArt);
        mProgressBar = view.findViewById(R.id.travelProgressBar);

        mCityQueryEditText.requestFocus();

        // Set up the adapter that will display results - POIs based on of user query
        mPlaces = new ArrayList<>();
        mAdapter = new PlacesAdapter(mPlaces, getContext(), this);
        mCityResultsRecyclerView.setAdapter(mAdapter);
        mCityResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCityResultsRecyclerView.setItemViewCacheSize(20);

        getSearchCityResults();
    }

    // Perform search when user click on Search button in keyboard
    public void getSearchCityResults() {
        mCityQueryEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    imm.hideSoftInputFromWindow(mCityQueryEditText.getWindowToken(), 0);
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
        String query = mCityQueryEditText.getText().toString();
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
                Toast.makeText(getContext(), R.string.pois_fail_to_query, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}