package com.meghpy.mylaundryapp;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthService {
    private SharedPreferences sharedPreferences;
    private Context context;

    public AuthService(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    // Add these methods to your AuthService class

    // Add this method
    public String getSessionCookie() {
        return sharedPreferences.getString("session_cookie", "");
    }

    // Update your login method to store session cookie
    public void saveLoginSession(String phone, String cookie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("phone", phone);
        editor.putString("session_cookie", cookie);
        editor.apply();
    }


    public String getUserName() {
        return sharedPreferences.getString("name", "");
    }

    public String getPhone() {
        return sharedPreferences.getString("phone", "");
    }

    public String getAddress() {
        return sharedPreferences.getString("address", "");
    }

    public String getSecurityAnswer() {
        return sharedPreferences.getString("security_answer", "");
    }

    public void saveUser(String name, String phone, String address, String security_answer) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("address", address);
        editor.putString("security_answer", security_answer);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return !getPhone().isEmpty();
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}