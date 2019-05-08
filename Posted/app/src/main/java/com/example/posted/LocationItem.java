package com.example.posted;

public class LocationItem {
    private String locationPhoto;
    private String locationName;
    private int rating;

    public LocationItem() {
        this.locationName = "sample_location_name";
        this.locationPhoto = "";
        this.rating = 0;
    }

    public String getLocationPhoto() {
        return locationPhoto;
    }

    public void setLocationPhoto(String locationPhoto) {
        this.locationPhoto = locationPhoto;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}
