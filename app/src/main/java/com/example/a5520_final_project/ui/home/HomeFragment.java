package com.example.a5520_final_project.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.a5520_final_project.MarkerData;
import com.example.a5520_final_project.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private MapView mMapView;
    private DatabaseReference markersRef;
    private List<String> selectedPhotos = new ArrayList<>();
    private static final int REQUEST_CODE_PHOTO_PICKER = 123;
    private LinearLayout photoContainer;

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
        // Get reference to the user's markers in Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            markersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("markers");
        }
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

    private void saveMarkerToDatabase(MarkerData markerData) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference markersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("markers");
            String markerId = markersRef.push().getKey();
            if (markerId != null) {
                markersRef.child(markerId).setValue(markerData);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        LatLng newyork = new LatLng(40, -74);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newyork));
        enableMarkerPlacement();
        loadUserMarkers();
    }

    public void enableMarkerPlacement() {
        // Set a click listener on the map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                // Create a dialog to confirm marker placement and customize marker details
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Marker");
                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_marker, null);
                builder.setView(dialogView);

                // Get references to UI components
                EditText markerNameEditText = dialogView.findViewById(R.id.marker_name_edit_text);
                EditText markerTextEdit = dialogView.findViewById(R.id.marker_text_edit_text);
                Button addPhotoButton = dialogView.findViewById(R.id.add_photo_button);
                CheckBox favoriteCheckBox = dialogView.findViewById(R.id.favorite_checkbox);
                photoContainer = dialogView.findViewById(R.id.photo_container);

                // Set click listener for the add photo button
                addPhotoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open the photo library to select photos
                        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        photoPickerIntent.setType("image/*");
                        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple photo selection
                        startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Photos"), REQUEST_CODE_PHOTO_PICKER);
                    }
                });

                builder.setPositiveButton("Add Marker", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the name entered by the user
                        String markerName = markerNameEditText.getText().toString();
                        // Get the text entered by the user
                        String markerText = markerTextEdit.getText().toString();
                        // Check if the marker should be marked as a favorite
                        boolean isFavorite = favoriteCheckBox.isChecked(); // Add this line

                        // Add a marker at the clicked location with the customized name
                        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(markerName));
                        MarkerData markerData = new MarkerData(markerName, System.currentTimeMillis(), latLng.latitude, latLng.longitude, markerText, selectedPhotos, isFavorite);
                        saveMarkerToDatabase(markerData); // Update this line

                        // Clear selected photos list for next use
                        selectedPhotos.clear();
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




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PHOTO_PICKER && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Get the URI of the selected photo(s)
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    // Multiple photos selected
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri selectedPhotoUri = clipData.getItemAt(i).getUri();
                        if (selectedPhotoUri != null) {
                            // Convert URI to string and store it
                            String selectedPhotoPath = selectedPhotoUri.toString();
                            // Add selected photo to the list of selected photos
                            selectedPhotos.add(selectedPhotoPath);
                            // Add image view to display the selected photo
                            ImageView imageView = new ImageView(getContext());
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            imageView.setImageURI(selectedPhotoUri);
                            photoContainer.addView(imageView);
                        }
                    }
                } else {
                    // Single photo selected
                    Uri selectedPhotoUri = data.getData();
                    if (selectedPhotoUri != null) {
                        // Convert URI to string and store it
                        String selectedPhotoPath = selectedPhotoUri.toString();
                        // Add selected photo to the list of selected photos
                        selectedPhotos.add(selectedPhotoPath);
                        // Add image view to display the selected photo
                        ImageView imageView = new ImageView(getContext());
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        imageView.setImageURI(selectedPhotoUri);
                        photoContainer.addView(imageView);
                    }
                }
            }
        }
    }




    private void loadUserMarkers() {
        if (markersRef == null) {
            return;
        }

        markersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot markerSnapshot : dataSnapshot.getChildren()) {
                    double latitude = markerSnapshot.child("latitude").getValue(Double.class);
                    double longitude = markerSnapshot.child("longitude").getValue(Double.class);
                    String title = markerSnapshot.child("title").getValue(String.class);

                    // Add marker to the map
                    LatLng markerPosition = new LatLng(latitude, longitude);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(markerPosition).title(title));

                    // Attach click listener to the marker
                    marker.setTag(markerSnapshot.getKey()); // Set tag to marker ID
                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker clickedMarker) {
                            // Retrieve marker ID from tag
                            String markerId = (String) clickedMarker.getTag();
                            if (markerId != null) {
                                // Retrieve marker data from Firebase based on marker ID
                                DatabaseReference markerDataRef = markersRef.child(markerId);
                                markerDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Retrieve marker data
                                        String name = (String) markerSnapshot.child("name").getValue();
                                        long timestamp = (long) markerSnapshot.child("timestamp").getValue();
                                        double latitude = (double) markerSnapshot.child("latitude").getValue();
                                        double longitude = (double) markerSnapshot.child("longitude").getValue();
                                        String text = (String) markerSnapshot.child("text").getValue();
                                        List<String> photos = new ArrayList<>();
                                        Boolean favorite = (Boolean) markerSnapshot.child("fav").getValue();
                                        for (DataSnapshot photoSnapshot : markerSnapshot.child("photos").getChildren()) {
                                            String photoUrl = photoSnapshot.getValue(String.class);
                                            photos.add(photoUrl);
                                        }

                                        // Display marker information in a dialog
                                        displayMarkerInformation(name, timestamp, latitude, longitude, text, photos, favorite);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle onCancelled
                                    }
                                });
                            }
                            return false; // Allow default behavior (open info window)
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e("HomeFragment", "Error loading markers from Firebase: " + databaseError.getMessage());
            }
        });
    }

    private void displayMarkerInformation(String name, long timestamp, double latitude, double longitude, String text, List<String> photos, boolean isFavorite) {
        // Create a dialog to display marker information
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Marker Information");

        // Inflate custom layout for marker information
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_marker_information, null);
        builder.setView(dialogView);

        // Display marker information in dialog
        TextView nameTextView = dialogView.findViewById(R.id.marker_name_text_view);
        TextView timestampTextView = dialogView.findViewById(R.id.marker_timestamp_text_view);
        TextView coordinatesTextView = dialogView.findViewById(R.id.marker_coordinates_text_view);
        TextView textTextView = dialogView.findViewById(R.id.marker_text_text_view);

        TextView favoriteTextView = dialogView.findViewById(R.id.marker_favorite_text_view);
        // Display other marker information as needed
        nameTextView.setText(name);
        timestampTextView.setText("Timestamp: " + timestamp); // Format timestamp as needed
        coordinatesTextView.setText("Latitude: " + latitude + ", Longitude: " + longitude);
        textTextView.setText("Text: " + text);
        favoriteTextView.setText("Favorite: " + isFavorite);

        builder.show();
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

}