package com.meghpy.mylaundryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {

    private List<Offer> offers;
    private OnOfferClickListener listener;

    public interface OnOfferClickListener {
        void onOfferClick(Offer offer);
    }

    public OfferAdapter(List<Offer> offers, OnOfferClickListener listener) {
        this.offers = offers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offers.get(position);
        holder.bind(offer);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOfferClick(offer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView imgOffer;
        private final TextView tvOfferTitle;
        private final TextView tvOfferSubtitle;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            imgOffer = itemView.findViewById(R.id.imgOffer);
            tvOfferTitle = itemView.findViewById(R.id.tvOfferTitle);
            tvOfferSubtitle = itemView.findViewById(R.id.tvOfferSubtitle);
        }

        public void bind(Offer offer) {
            imgOffer.setImageResource(offer.getImageRes());
            tvOfferTitle.setText(offer.getTitle());
            tvOfferSubtitle.setText(offer.getSubtitle());
        }
    }

    // Offer model class (should be in a separate file in production)
    public static class Offer {
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
}