package com.meghpy.mylaundryapp;

import android.app.ProgressDialog;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
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
                        e.printStackTrace();
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

            // Only allow cancellation for pending orders
            holder.btnCancel.setVisibility(
                    order.getStatus().equals("Pending") ? View.VISIBLE : View.GONE
            );

            holder.btnCancel.setOnClickListener(v -> cancelOrder(order.getId()));
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        private void cancelOrder(int orderId) {
            String url = "https://meghpy.com/apps/LaundryApp/orders.php?action=cancel&order_id=" + orderId;

            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    url,
                    response -> {
                        Toast.makeText(OrderHistoryActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                        loadOrders(); // Refresh the list
                    },
                    error -> Toast.makeText(OrderHistoryActivity.this, "Cancellation failed", Toast.LENGTH_SHORT).show()
            );

            NetworkHelper.getInstance(OrderHistoryActivity.this).addToRequestQueue(request);
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