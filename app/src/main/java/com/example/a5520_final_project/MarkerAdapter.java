package com.example.a5520_final_project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
        private TextView nameTextView, timestampTextView, coordinatesTextView, textTextView, favorite;
        private LinearLayout photoContainer;

        public MarkerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_name);
            timestampTextView = itemView.findViewById(R.id.text_timestamp);
            coordinatesTextView = itemView.findViewById(R.id.text_coordinates);
            textTextView = itemView.findViewById(R.id.text_text);
            photoContainer = itemView.findViewById(R.id.photo_container);
            favorite = itemView.findViewById(R.id.favorite);
        }

        public void bind(MarkerData marker) {
            nameTextView.setText(marker.getName());
            timestampTextView.setText("Date Added: " + formatDate(marker.getTimestamp()));
            coordinatesTextView.setText("Lat: " + marker.getLatitude() + ", Long: " + marker.getLongitude());
            textTextView.setText("Text: " + marker.getText());
            favorite.setText("Favorite: " + marker.getFav());

            // Clear previous photos
            photoContainer.removeAllViews();
            photoContainer.setOrientation(LinearLayout.VERTICAL);

            // Add photos to the photo container
            for (String photoUrl : marker.getPhotos()) {
                ImageView imageView = new ImageView(itemView.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                layoutParams.setMargins(0, 0, 0, 2);
                imageView.setLayoutParams(layoutParams);
                Picasso.get().load(photoUrl).into(imageView);
                photoContainer.addView(imageView);
            }
        }


        private String formatDate(long timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return dateFormat.format(new Date(timestamp));
        }
    }
}


