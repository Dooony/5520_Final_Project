package com.example.a5520_final_project;

public class MarkerData {
    private String name;
    private long timestamp;
    private double latitude;
    private double longitude;

    public MarkerData() {
        // Default constructor required for Firebase
    }

    public MarkerData(String name, long timestamp, double latitude, double longitude) {
        this.name = name;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

