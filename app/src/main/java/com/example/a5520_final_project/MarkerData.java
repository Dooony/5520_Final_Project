package com.example.a5520_final_project;

import java.util.List;


public class MarkerData {
    private String name;
    private long timestamp;
    private double latitude;
    private double longitude;
    private String text;
    private List<String> photos;

    private Boolean favorite;

    public MarkerData() {
        // Default constructor required for Firebase
    }

    public MarkerData(String name, long timestamp, double latitude, double longitude, String text, List<String> photos, Boolean favorite) {
        this.name = name;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.text = text;
        this.photos = photos;
        this.favorite = favorite;
    }

    // Getters and setters
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Boolean getFav(){
        return favorite;
    }
    public void setFav(Boolean favorite){
        this.favorite = favorite;
    }

}


