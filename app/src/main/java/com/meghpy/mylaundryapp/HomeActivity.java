package com.meghpy.mylaundryapp;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.view.View;


public class HomeActivity extends AppCompatActivity {

    // UI Components
    private TextView tvWelcome;
    private EditText etSearch;
    private TextView tvServices, tvPopular, tvOffers;
    private RecyclerView rvServices, rvPopularLaundry, rvOffers;
    private RecyclerView rvLaundries;
    private LaundryAdapter adapter;
    private ProgressBar progressBar;
    private BottomNavigationView bottom_nav;
    // Adapters
//    private ServiceAdapter serviceAdapter;
//    private LaundryAdapter laundryAdapter;
//    private OfferAdapter offerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Your XML layout

        // Initialize all views
        initializeViews();

        // Setup adapters and data
        rvLaundries = findViewById(R.id.rvPopularLaundry);
        progressBar = findViewById(R.id.progressBar);
        bottom_nav = findViewById(R.id.bottom_nav);

        bottom_nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.action_home){
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                }
                else if (item.getItemId()==R.id.action_orders){
                    startActivity(new Intent(HomeActivity.this, OrderHistoryActivity.class));
                } else if (item.getItemId()==R.id.action_profile){
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                } else if (item.getItemId()==R.id.action_laundry){
                    startActivity(new Intent(HomeActivity.this, LaundryListActivity.class));
                }

                return false;
            }
        });



        rvLaundries.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new LaundryAdapter(new ArrayList<>(), this);
        rvLaundries.setAdapter(adapter);

        fetchLaundries();
        setupServicesRecyclerView();
        setupOffersRecyclerView();

        // Setup search functionality
        setupSearch();
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        etSearch = findViewById(R.id.etSearch);
        tvServices = findViewById(R.id.tvServices);
        tvPopular = findViewById(R.id.tvPopular);
        tvOffers = findViewById(R.id.tvOffers);

        // RecyclerViews
        rvServices = findViewById(R.id.rvServices);
        rvPopularLaundry = findViewById(R.id.rvPopularLaundry);
        rvOffers = findViewById(R.id.rvOffers);

        // Set layout managers
        rvServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvPopularLaundry.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvOffers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    // In your HomeActivity or Fragment:
    private void setupServicesRecyclerView() {
        List<ServiceAdapter.Service> services = new ArrayList<>();
        services.add(new ServiceAdapter.Service("Wash", R.drawable.wash));
        services.add(new ServiceAdapter.Service("Iron", R.drawable.iron));
        services.add(new ServiceAdapter.Service("Dry Clean", R.drawable.outline_air_24));
        services.add(new ServiceAdapter.Service("Fold", R.drawable.premium));

        ServiceAdapter adapter = new ServiceAdapter(services, service -> {
            // Handle service click
            startActivity(new Intent(HomeActivity.this, NewOrderActivity.class));
            Toast.makeText(this, service.getName() + " clicked", Toast.LENGTH_SHORT).show();
            // Or navigate to another activity
        });

        RecyclerView rvServices = findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvServices.setAdapter(adapter);
    }
    //--------------------------------------------------------------------------

    // In your HomeActivity or Fragment:
    private void fetchLaundries() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.BASE_URL + "/laundry.php?action=list";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        List<Laundry> laundries = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject json = response.getJSONObject(i);
                            laundries.add(new Laundry(
                                    json.getInt("id"),
                                    json.getString("name"),
                                    json.getString("address"),
                                    json.getString("phone"),
                                    json.getString("image_url"),
                                    (float)json.getDouble("rating")
                            ));
                        }
                        adapter = new LaundryAdapter(laundries, HomeActivity.this);
                        rvLaundries.setAdapter(adapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load laundries", Toast.LENGTH_SHORT).show();
                }
        );

        // Add request to queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    //---------------------------------------------------------

    // In your HomeActivity or Fragment:
    private void setupOffersRecyclerView() {
        List<OfferAdapter.Offer> offers = new ArrayList<>();
        offers.add(new OfferAdapter.Offer(
                "10% Flat Discount",
                "On all kinds of laundry services",
                R.drawable.ic_offer
        ));
        offers.add(new OfferAdapter.Offer(
                "Free Pickup",
                "For orders above ₹500",
                R.drawable.ic_pickup
        ));
        offers.add(new OfferAdapter.Offer(
                "Express Service",
                "24 hour delivery",
                R.drawable.ic_express
        ));

        OfferAdapter adapter = new OfferAdapter(offers, offer -> {
            // Handle offer click
            Toast.makeText(this, offer.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
            // Or navigate to offer details
        });

        RecyclerView rvOffers = findViewById(R.id.rvOffers);
        rvOffers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvOffers.setAdapter(adapter);
    }



    private void setupSearch() {
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            // Handle search action
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                // Implement your search functionality here
                // For example: filterLaundries(query);
            }
            return false;
        });
    }
    //===========================================================================
}