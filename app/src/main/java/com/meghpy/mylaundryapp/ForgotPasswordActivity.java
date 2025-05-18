package com.meghpy.mylaundryapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etPhone, etSecurityAnswer;
    private TextView tvSecurityQuestion;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etPhone = findViewById(R.id.etPhone);
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer);
        tvSecurityQuestion = findViewById(R.id.tvSecurityQuestion);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String phone = etPhone.getText().toString().trim();
        String answer = etSecurityAnswer.getText().toString().trim();

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            return;
        }

        if (answer.isEmpty()) {
            etSecurityAnswer.setError("Security answer is required");
            return;
        }

        JSONObject params = new JSONObject();
        try {
            params.put("phone", phone);
            params.put("answer", answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://meghpy.com/apps/LaundryApp/reset_password.php",
                params,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this,
                                    "Password reset to default: Meghpy1@#\nPlease change after login",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
        );

        NetworkHelper.getInstance(this).addToRequestQueue(request);
    }
}