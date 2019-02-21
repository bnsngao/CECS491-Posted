package com.example.posted;

public class ProfileItem {
    int profilePhoto;
    int ratingNumber;
    String profileName;

    public ProfileItem() {
        this.profileName = "sample_user";
        this.ratingNumber = 2;
    }

    public ProfileItem(int profilePhoto, int ratingNumber, String profileName) {
        this.profilePhoto = profilePhoto;
        this.ratingNumber = ratingNumber;
        this.profileName = profileName;
    }

    public int getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(int profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public int getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(int ratingNumber) {
        this.ratingNumber = ratingNumber;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}
