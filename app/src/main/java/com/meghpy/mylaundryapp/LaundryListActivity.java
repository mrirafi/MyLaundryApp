package com.meghpy.mylaundryapp;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LaundryListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LaundryAdapter adapter;
    private List<Laundry> laundryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_list); // Your XML layout

        // Initialize toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaundryAdapter(laundryList, this);
        recyclerView.setAdapter(adapter);

        // Fetch laundry data
        fetchLaundryData();
    }

    private void fetchLaundryData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.BASE_URL + "/laundry.php?action=list";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        List<Laundry> newLaundryList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject json = response.getJSONObject(i);
                            Laundry laundry = new Laundry(
                                    json.getInt("id"),
                                    json.getString("name"),
                                    json.getString("address"),
                                    json.getString("phone"),
                                    json.getString("image_url"),
                                    (float) json.getDouble("rating")
                            );
                            newLaundryList.add(laundry);
                        }
                        laundryList.clear();
                        laundryList.addAll(newLaundryList);
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        Log.e("LaundryList", "JSON parsing error", e);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load laundry shops", Toast.LENGTH_SHORT).show();
                    Log.e("LaundryList", "Network error: " + error.getMessage());
                    if (error.networkResponse != null) {
                        Log.e("LaundryList", "Status code: " + error.networkResponse.statusCode);
                    }
                }
        );

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000, // 10 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}