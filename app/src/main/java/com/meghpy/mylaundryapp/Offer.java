package com.meghpy.mylaundryapp;

// Offer.java
public class Offer {
    private String title;
    private String subtitle;
    private int imageRes;

    public Offer(String title, String subtitle, int imageRes) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getImageRes() {
        return imageRes;
    }
}
