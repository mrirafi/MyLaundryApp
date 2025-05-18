package com.meghpy.mylaundryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class LaundryDetailActivity extends AppCompatActivity {
    private int laundryId;
    private ImageView imgLaundry;
    private TextView tvLaundryName, tvLaundryAddress, tvLaundryPhone;
    private RatingBar ratingBar;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laundry_detail);

        // Initialize views
        progressBar = findViewById(R.id.progressBar);
        imgLaundry = findViewById(R.id.imgLaundry);
        tvLaundryName = findViewById(R.id.tvLaundryName);
        tvLaundryAddress = findViewById(R.id.tvLaundryAddress);
        tvLaundryPhone = findViewById(R.id.tvLaundryPhone);
        ratingBar = findViewById(R.id.ratingBar);


        // Get laundry ID from intent
        laundryId = getIntent().getIntExtra("laundry_id", -1);
        if (laundryId == -1) {
            Toast.makeText(this, "Invalid laundry selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        fetchLaundryDetails();

    }

    private void fetchLaundryDetails() {
        progressBar.setVisibility(View.VISIBLE);
        String url = Constants.BASE_URL + "/laundry.php?action=details&id=" + laundryId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        // Check if response has error
                        if (response.has("status") && response.getString("status").equals("error")) {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Load image with Picasso
                        Picasso.get()
                                .load(response.getString("image_url"))
                                .placeholder(R.drawable.meghpy) // Your placeholder image
                                .error(R.drawable.meghpy) // Image to show if loading fails
                                .into(imgLaundry);

                        // Set text values
                        tvLaundryName.setText(response.getString("name"));
                        tvLaundryAddress.setText(response.getString("address"));
                        tvLaundryPhone.setText(response.getString("phone"));
                        ratingBar.setRating((float) response.getDouble("rating"));

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing laundry details", Toast.LENGTH_SHORT).show();
                        Log.e("LaundryDetail", "JSON Error: " + e.getMessage());
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to load laundry details", Toast.LENGTH_SHORT).show();
                    Log.e("LaundryDetail", "Volley Error: " + error.getMessage());

                    // Show placeholder data if network fails
                    imgLaundry.setImageResource(R.drawable.meghpy);
                    tvLaundryName.setText("Laundry Name");
                    tvLaundryAddress.setText("Address not available");
                    tvLaundryPhone.setText("Phone not available");
                    ratingBar.setRating(0f);
                }
        );

        // Set timeout to 15 seconds
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    //-----------------------------------------------------------------------------



    //---------------------------------------------------------------------



}