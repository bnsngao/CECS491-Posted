package com.example.posted;

public class LocationItem {
    private String locationPhoto;
    private String locationName;
    private String locationID;
    private float rating;

    public LocationItem() {
        this.locationName = "sample_location_name";
        this.locationPhoto = "";
        this.rating = 0;
    }

    public LocationItem(String locationPhoto, String locationName, String locationID, float rating){
        this.locationPhoto = locationPhoto;
        this.locationName = locationName;
        this.locationID = locationID;
        this.rating =rating;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

}
