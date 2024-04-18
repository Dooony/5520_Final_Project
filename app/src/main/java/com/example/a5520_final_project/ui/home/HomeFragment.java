package com.example.a5520_final_project.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.a5520_final_project.R;
import com.example.a5520_final_project.databinding.FragmentHomeBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private MapView mMapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the MapView
        mMapView = root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        // Get ViewModel instance
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize search button and edit text
        Button searchButton = view.findViewById(R.id.search_button);
        EditText searchEditText = view.findViewById(R.id.search_edit_text);

        // Set click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the search query from the edit text
                String locationName = searchEditText.getText().toString();

                // Use a geocoding service to convert the location name into coordinates
                // Update the map to display the location
                performGeocoding(locationName);
            }
        });

        // Other initialization code for the map
    }

    private void performGeocoding(String locationName) {
        if (getContext() == null) {
            return;
        }

        // Create a Geocoder instance
        Geocoder geocoder = new Geocoder(getContext());

        try {
            // Perform geocoding to get the list of addresses for the given location name
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

            if (addresses != null && !addresses.isEmpty()) {
                // Get the first address from the list (most relevant one)
                Address address = addresses.get(0);

                // Extract latitude and longitude from the address
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Move the camera to the searched location and add a marker
                LatLng searchedLocation = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 14));
                mMap.addMarker(new MarkerOptions().position(searchedLocation).title(locationName));
            } else {
                // If no address found, show a toast indicating that the location could not be found
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // Handle IOException
            e.printStackTrace();
            Toast.makeText(getContext(), "Geocoding error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng newyork = new LatLng(40, -74);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newyork));
        enableMarkerPlacement();
    }

    private BitmapDescriptor
    BitmapFromVector(Context context, int vectorResId)
    {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void enableMarkerPlacement() {
        // Set a click listener on the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                // Create a dialog to confirm marker placement and customize marker name
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Marker");
                builder.setMessage("Do you want to add a marker at this location?");

                // Add an EditText for customizing the marker name
                final EditText markerNameEditText = new EditText(getContext());
                markerNameEditText.setHint("Marker Name");
                builder.setView(markerNameEditText);

                builder.setPositiveButton("Add Marker", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the name entered by the user
                        String markerName = markerNameEditText.getText().toString();

                        // Add a marker at the clicked location with the customized name
                        mMap.addMarker(new MarkerOptions().position(latLng).title(markerName));
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User canceled, do nothing
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }



}
