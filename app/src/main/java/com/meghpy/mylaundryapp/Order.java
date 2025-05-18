package com.meghpy.mylaundryapp;

import android.os.Parcel;
import android.os.Parcelable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Order implements Parcelable {
    private int id;
    private String phone;
    private String service;
    private String dressType;
    private int quantity;
    private String pickupTime;
    private String address;
    private String status;
    private String createdAt;

    private String laundryName;
    private String date;
    private double total;
    private String items;

    public Order(int id, String service,  int quantity, String pickupTime, String address,
                 String status, String createdAt) {
        this.id = id;
        this.phone = phone;
        this.service = service;
        this.dressType = dressType;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
    }

    protected Order(Parcel in) {
        id = in.readInt();
        phone = in.readString();
        service = in.readString();
        dressType = in.readString();
        quantity = in.readInt();
        pickupTime = in.readString();
        address = in.readString();
        status = in.readString();
        createdAt = in.readString();
    }

    // Getters and setters for all fields
    public int getId() { return id; }
    public String getPhone() { return phone; }
    public String getService() { return service; }
    public String getDressType() { return dressType; }
    public int getQuantity() { return quantity; }
    public String getPickupTime() { return pickupTime; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public void setItems(String items) {
        this.items = items;
    }


    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(phone);
        dest.writeString(service);
        dest.writeString(dressType);
        dest.writeInt(quantity);
        dest.writeString(pickupTime);
        dest.writeString(address);
        dest.writeString(status);
        dest.writeString(createdAt);
    }

    public String getFormattedDate() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
            Date date = inputFormat.parse(createdAt);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return createdAt;
        }
    }
}

