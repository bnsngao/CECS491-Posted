package com.example.posted;

public class LocationItem {
    int locationPhoto;
    String locationName;
    String locationSummary;

    public LocationItem() {
        this.locationName = "sample_location_name";
        this.locationSummary = "sample_location_summary";
    }

    public LocationItem(int locationPhoto, String locationName, String locationSummary) {
        this.locationPhoto = locationPhoto;
        this.locationName = locationName;
        this.locationSummary = locationSummary;
    }

    public int getLocationPhoto() {
        return locationPhoto;
    }

    public void setLocationPhoto(int locationPhoto) {
        this.locationPhoto = locationPhoto;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationSummary() {
        return locationSummary;
    }

    public void setLocationSummary(String locationSummary) {
        this.locationSummary = locationSummary;
    }
}
