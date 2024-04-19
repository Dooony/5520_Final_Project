package com.example.a5520_final_project.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
                        String name = markerSnapshot.child("name").getValue(String.class);
                        long timestamp = markerSnapshot.child("timestamp").getValue(Long.class);
                        double latitude = markerSnapshot.child("latitude").getValue(Double.class);
                        double longitude = markerSnapshot.child("longitude").getValue(Double.class);

                        MarkerData marker = new MarkerData(name, timestamp, latitude, longitude);
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
