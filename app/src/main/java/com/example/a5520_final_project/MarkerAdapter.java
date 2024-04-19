package com.example.a5520_final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MarkerAdapter extends RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder> {
    private List<MarkerData> markerList;

    public MarkerAdapter(List<MarkerData> markerList) {
        this.markerList = markerList;
    }

    @NonNull
    @Override
    public MarkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marker, parent, false);
        return new MarkerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerViewHolder holder, int position) {
        MarkerData marker = markerList.get(position);
        holder.bind(marker);
    }

    @Override
    public int getItemCount() {
        return markerList.size();
    }

    static class MarkerViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView, timestampTextView, coordinatesTextView;

        public MarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            timestampTextView = itemView.findViewById(R.id.text_timestamp);
            coordinatesTextView = itemView.findViewById(R.id.text_coordinates);
        }

        public void bind(MarkerData marker) {
            nameTextView.setText(marker.getName());
            timestampTextView.setText("Added: " + formatDate(marker.getTimestamp()));
            coordinatesTextView.setText("Lat: " + marker.getLatitude() + ", Long: " + marker.getLongitude());
        }

        private String formatDate(long timestamp) {
            // Convert timestamp to formatted date string (e.g., "Apr 17, 2024")
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return dateFormat.format(new Date(timestamp));
        }
    }
}

