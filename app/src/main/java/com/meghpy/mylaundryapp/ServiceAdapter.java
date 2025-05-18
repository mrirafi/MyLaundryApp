package com.meghpy.mylaundryapp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<Service> services;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> services, OnServiceClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView imgService;
        private final TextView tvServiceName;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            imgService = itemView.findViewById(R.id.imgService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
        }

        public void bind(Service service) {
            imgService.setImageResource(service.getIconRes());
            tvServiceName.setText(service.getName());
        }
    }

    // Service model class (should be in a separate file in production)
    public static class Service {
        private String name;
        private int iconRes;

        public Service(String name, int iconRes) {
            this.name = name;
            this.iconRes = iconRes;
        }

        public String getName() {
            return name;
        }

        public int getIconRes() {
            return iconRes;
        }
    }
}