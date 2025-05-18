package com.meghpy.mylaundryapp;

// Laundry model class (should be in a separate file in production)
public  class Laundry {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String imageUrl;
    private float rating;

    public Laundry(int id, String name, String address, String phone, String imageUrl, float rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getImageUrl() { return imageUrl; }
    public float getRating() { return rating; }
}