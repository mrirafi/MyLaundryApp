package com.meghpy.mylaundryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        authService = new AuthService(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        String url = "https://meghpy.com/apps/LaundryApp/orders.php?action=get&phone=" + authService.getPhone();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Order> orders = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject orderJson = response.getJSONObject(i);
                            Order order = new Order(
                                    orderJson.getInt("id"),
                                    orderJson.getString("service"),
                                    orderJson.getInt("quantity"),
                                    orderJson.getString("pickup_time"),
                                    orderJson.getString("address"),
                                    orderJson.getString("status"),
                                    orderJson.getString("created_at")
                            );
                            orders.add(order);
                        }
                        adapter = new OrderAdapter(orders);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to load orders", Toast.LENGTH_SHORT).show()
        );

        NetworkHelper.getInstance(this).addToRequestQueue(request);
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private List<Order> orders;

        public OrderAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orders.get(position);

            holder.tvService.setText("Service: " + order.getService());
            holder.tvQuantity.setText("Quantity: " + order.getQuantity());
            holder.tvPickup.setText("Pickup: " + order.getPickupTime());
            holder.tvStatus.setText("Status: " + order.getStatus());

            if (order.getStatus().equalsIgnoreCase("Pending")) {
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setOnClickListener(v -> {
                    new AlertDialog.Builder(OrderHistoryActivity.this)
                            .setTitle("Cancel Order")
                            .setMessage("Are you sure you want to cancel this order?")
                            .setPositiveButton("Yes", (dialog, which) -> cancelOrder(String.valueOf(order.getId())))
                            .setNegativeButton("No", null)
                            .show();
                });
            } else {
                holder.btnCancel.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        private void cancelOrder(String orderId) {
            String url = "https://meghpy.com/apps/LaundryApp/orders.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            // Optional: Log the server response
                            // Log.d("CancelOrderResponse", response);

                            JSONObject res = new JSONObject(response.trim());
                            String status = res.getString("status");
                            String message = res.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                Toast.makeText(OrderHistoryActivity.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                                loadOrders(); // Refresh the list
                            } else {
                                Toast.makeText(OrderHistoryActivity.this, "Cancel failed: " + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OrderHistoryActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(OrderHistoryActivity.this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("action", "cancel");                     // ✅ Required for PHP
                    params.put("order_id", orderId);                    // ✅ Order ID to cancel
                    params.put("phone", authService.getPhone());        // ✅ To verify order ownership
                    return params;
                }
            };

            Volley.newRequestQueue(OrderHistoryActivity.this).add(request);
        }


        class OrderViewHolder extends RecyclerView.ViewHolder {
            TextView tvService, tvQuantity, tvPickup, tvStatus;
            Button btnCancel;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                tvService = itemView.findViewById(R.id.tvService);
                tvQuantity = itemView.findViewById(R.id.tvQuantity);
                tvPickup = itemView.findViewById(R.id.tvPickup);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                btnCancel = itemView.findViewById(R.id.btnCancel);
            }
        }
    }
}
