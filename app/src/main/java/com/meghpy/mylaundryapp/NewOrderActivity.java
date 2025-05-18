package com.meghpy.mylaundryapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewOrderActivity extends AppCompatActivity {
    private Spinner spinnerService, spinnerQuantity, spinnerDressType;
    private EditText etPickupTime, etAddress;
    private TextView tvPrice;
    private AuthService authService;
    private List<DressType> dressTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        authService = new AuthService(this);
        initializeViews();
        setupSpinners();
        setupDateTimePicker();
        setupSubmitButton();
    }

    private void initializeViews() {
        spinnerService = findViewById(R.id.spinnerService);
        spinnerQuantity = findViewById(R.id.spinnerQuantity);
        spinnerDressType = findViewById(R.id.spinnerDressType);
        etPickupTime = findViewById(R.id.etPickupTime);
        etAddress = findViewById(R.id.etAddress);
        tvPrice = findViewById(R.id.tvPrice);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        // Set default address
        etAddress.setText(authService.getAddress());
    }

    private void setupSpinners() {

        ArrayAdapter<CharSequence> serviceAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.services_array,
                android.R.layout.simple_spinner_item
        );
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(serviceAdapter);

        // Quantity spinner
        ArrayAdapter<CharSequence> quantityAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.quantities_array,
                android.R.layout.simple_spinner_item
        );
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuantity.setAdapter(quantityAdapter);

        // Dress type spinner
        initializeDressTypes();
        DressTypeAdapter dressAdapter = new DressTypeAdapter(this, dressTypes);
        spinnerDressType.setAdapter(dressAdapter);

        // Update price when selections change
        spinnerDressType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //==================================================
    public class DressTypeAdapter extends ArrayAdapter<DressType> {
        public DressTypeAdapter(Context context, List<DressType> dressTypes) {
            super(context, android.R.layout.simple_spinner_item, dressTypes);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setText(getItem(position).getName());
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setText(getItem(position).getName());
            return view;
        }
    }

    private void initializeDressTypes() {
        dressTypes.add(new DressType("Shirt", R.drawable.outline_air_24, 10.0));
        dressTypes.add(new DressType("Pant", R.drawable.outline_air_24, 15.0));
        dressTypes.add(new DressType("T-Shirt", R.drawable.outline_air_24, 8.0));
        dressTypes.add(new DressType("Saree", R.drawable.outline_air_24, 20.0));
        dressTypes.add(new DressType("Jeans", R.drawable.outline_air_24, 18.0));
        dressTypes.add(new DressType("Kurta", R.drawable.outline_air_24, 12.0));
    }

    private void updatePrice() {
        DressType selectedDress = (DressType) spinnerDressType.getSelectedItem();
        int quantity = Integer.parseInt(spinnerQuantity.getSelectedItem().toString().split(" ")[0]);
        double total = selectedDress.getPrice() * quantity;
        tvPrice.setText(String.format(Locale.getDefault(), "Price: ₹%.2f", total));
    }

    private void setupDateTimePicker() {
        etPickupTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    NewOrderActivity.this,
                    (view, year, month, day) -> {
                        TimePickerDialog timePicker = new TimePickerDialog(
                                NewOrderActivity.this,
                                (view1, hour, minute) -> {
                                    String selectedTime = String.format(Locale.getDefault(),
                                            "%02d/%02d/%d %02d:%02d",
                                            day, month + 1, year, hour, minute);
                                    etPickupTime.setText(selectedTime);
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                        );
                        timePicker.show();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });
    }

    private void setupSubmitButton() {
        Button btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(v -> submitOrder());
    }

    private void submitOrder() {
        String service = spinnerService.getSelectedItem().toString();
        DressType selectedDress = (DressType) spinnerDressType.getSelectedItem();
        String quantity = spinnerQuantity.getSelectedItem().toString().split(" ")[0];
        String pickupTime = etPickupTime.getText().toString();
        String address = etAddress.getText().toString();

        if (pickupTime.isEmpty()) {
            Toast.makeText(this, "Please select pickup time", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("service", service);
            params.put("dress_type", selectedDress.getName());
            params.put("quantity", quantity);
            params.put("pickup_time", pickupTime);
            params.put("address", address);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating order", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://meghpy.com/apps/LaundryApp/orders.php?action=create&phone=" + authService.getPhone();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(NewOrderActivity.this,
                                    "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(NewOrderActivity.this,
                                    response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(NewOrderActivity.this,
                                "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(NewOrderActivity.this,
                        "Failed to place order", Toast.LENGTH_SHORT).show()
        );

        NetworkHelper.getInstance(this).addToRequestQueue(request);
    }
}