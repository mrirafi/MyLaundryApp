package com.meghpy.mylaundryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etPhone, etAddress, etSecurityAnswer;
    private AuthService authService;
    Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class)));

        // Initialize AuthService
        authService = new AuthService(this);

        // Initialize views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        Button btnSave = findViewById(R.id.btnSave);

        // Load current user data
        loadProfileData();

        // Set click listener
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadProfileData() {
        etName.setText(authService.getUserName());
        etPhone.setText(authService.getPhone());
        etAddress.setText(authService.getAddress());
        etSecurityAnswer.setText(authService.getSecurityAnswer());

        // Make phone number non-editable
        etPhone.setEnabled(false);
    }

    private void updateProfile() {
        // Get input values
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = authService.getPhone();
        String security_answer = etSecurityAnswer.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            etName.setError("Name cannot be empty");
            return;
        }

        if (address.isEmpty()) {
            etAddress.setError("Address cannot be empty");
            return;
        }

        if (security_answer.isEmpty()) {
            etSecurityAnswer.setError("Security answer cannot be empty");
            return;
        }

        // Create request body
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
            params.put("address", address);
            params.put("phone", phone);
            params.put("security_answer", security_answer);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create and send request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://meghpy.com/apps/LaundryApp/update_profile.php",
                params,
                response -> handleUpdateResponse(response, name, address, security_answer),
                error -> Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        NetworkHelper.getInstance(this).addToRequestQueue(request);
    }

    private void handleUpdateResponse(JSONObject response, String name, String address, String security_answer) {
        try {
            if (response.getString("status").equals("success")) {
                // Update local profile data
                authService.saveUser(name, authService.getPhone(), address, security_answer);
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                String errorMsg = response.optString("message", "Update failed");
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
        }
    }
}