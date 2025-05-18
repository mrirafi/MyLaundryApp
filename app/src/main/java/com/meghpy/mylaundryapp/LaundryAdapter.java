package com.meghpy.mylaundryapp;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.squareup.picasso.Picasso;


public class LaundryAdapter extends RecyclerView.Adapter<LaundryAdapter.LaundryViewHolder> {
    private List<Laundry> laundries;
    private Context context;

    public LaundryAdapter(List<Laundry> laundries, Context context) {
        this.laundries = laundries;
        this.context = context;
    }

    @NonNull
    @Override
    public LaundryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laundry, parent, false);
        return new LaundryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaundryViewHolder holder, int position) {
        Laundry laundry = laundries.get(position);

        // Load image using Picasso
        Picasso.get()
                .load(laundry.getImageUrl())
                .placeholder(R.drawable.meghpy)
                .into(holder.imgLaundry);

        holder.tvLaundryName.setText(laundry.getName());
        holder.tvLaundryAddress.setText(laundry.getAddress());
        holder.ratingBar.setRating(laundry.getRating());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LaundryDetailActivity.class);
            intent.putExtra("laundry_id", laundry.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laundries.size();
    }

    public static class LaundryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLaundry;
        TextView tvLaundryName, tvLaundryAddress;
        RatingBar ratingBar;

        public LaundryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLaundry = itemView.findViewById(R.id.imgLaundry);
            tvLaundryName = itemView.findViewById(R.id.tvLaundryName);
            tvLaundryAddress = itemView.findViewById(R.id.tvLaundryAddress);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}