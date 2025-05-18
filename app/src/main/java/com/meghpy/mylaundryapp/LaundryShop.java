package com.meghpy.mylaundryapp;

// LaundryShop.java
public class LaundryShop {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String imageUrl;
    private float rating;

    // Constructor, getters and setters
    public LaundryShop(int id, String name, String address, String phone, String imageUrl, float rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    // Add getters here...
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getImageUrl() { return imageUrl; }
    public float getRating() { return rating; }
}