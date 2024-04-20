package com.example.a5520_final_project.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5520_final_project.MarkerAdapter;
import com.example.a5520_final_project.MarkerData;
import com.example.a5520_final_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private MarkerAdapter adapter;
    private List<MarkerData> markerList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        markerList = new ArrayList<>();
        adapter = new MarkerAdapter(markerList);
        recyclerView.setAdapter(adapter);

        loadMarkersFromFirebase();

        return rootView;
    }

    private void loadMarkersFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference markersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("markers");

            markersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                        String name = (String) markerSnapshot.child("name").getValue();
                        long timestamp = (long) markerSnapshot.child("timestamp").getValue();
                        double latitude = (double) markerSnapshot.child("latitude").getValue();
                        double longitude = (double) markerSnapshot.child("longitude").getValue();
                        String text = (String) markerSnapshot.child("text").getValue();
                        List<String> photos = new ArrayList<>();
                        for (DataSnapshot photoSnapshot : markerSnapshot.child("photos").getChildren()) {
                            String photoUrl = photoSnapshot.getValue(String.class);
                            photos.add(photoUrl);
                        }

                        MarkerData marker = new MarkerData(name, timestamp, latitude, longitude, text, photos);
                        markerList.add(marker);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("DashboardFragment", "Error loading markers from Firebase: " + databaseError.getMessage());
                }
            });
        }
    }
}
