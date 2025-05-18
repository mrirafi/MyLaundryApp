package com.meghpy.mylaundryapp;

public class DressType {
    private String name;
    private int iconResId;
    private double price;

    public DressType(String name, int iconResId, double price) {
        this.name = name;
        this.iconResId = iconResId;
        this.price = price;
    }

    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public double getPrice() { return price; }
}