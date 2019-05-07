package com.example.posted;

import java.util.HashMap;

public class Profile {
    public String display_name;
    public boolean guide_status;
    public int rating;
    public int profile_photo;
    public HashMap<String, Boolean> food_prefs;
    public HashMap<String, Boolean> other_prefs;

    public Profile() {
    }

    public Profile(String display_name,int rating, int profile_photo, boolean guide_status, HashMap<String, Boolean> food_prefs, HashMap<String, Boolean> other_prefs) {
        this.display_name = display_name;
        this.rating = rating;
        this.profile_photo = profile_photo;
        this.guide_status = guide_status;
        this.food_prefs = food_prefs;
        this.other_prefs = other_prefs;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplayName() {
        return display_name;
    }

    public int getRating() {
        return rating;
    }

    public int getProfile_photo() {
        return profile_photo;
    }

    public boolean isGuide() {
        return guide_status;
    }

    public void setGuideStatus(boolean guide_status) {
        this.guide_status = guide_status;
    }

    public HashMap<String, Boolean> getFoodPrefs() {
        return food_prefs;
    }

    public void setFoodPrefs(HashMap<String, Boolean> food_prefs) {
        this.food_prefs = food_prefs;
    }

    public HashMap<String, Boolean> getOtherPrefs() {
        return other_prefs;
    }

    public void setOtherPrefs(HashMap<String, Boolean> other_prefs) {
        this.other_prefs = other_prefs;
    }
}
