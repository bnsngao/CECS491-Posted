package com.example.posted;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile {
    public String display_name;
    public boolean guide_status;
    public float rating;
    public String profile_photo;
    public String uid;
    public HashMap<String, Boolean> food_prefs;
    public HashMap<String, Boolean> other_prefs;
    public HashMap<String, String> locations;
    public ArrayList<String> similarities;

    public Profile() {
    }

    public Profile(String display_name, String uid, float rating, String profile_photo, boolean guide_status, HashMap<String, Boolean> food_prefs, HashMap<String, Boolean> other_prefs, HashMap<String, String> locations, ArrayList<String> similarities) {
        this.display_name = display_name;
        this.uid = uid;
        this.rating = rating;
        this.profile_photo = profile_photo;
        this.guide_status = guide_status;
        this.food_prefs = food_prefs;
        this.other_prefs = other_prefs;
        this.locations = locations;
        this.similarities = similarities;
    }

    public ArrayList<String> getSimilarities() {
        return similarities;
    }

    public void setSimilarities(ArrayList<String> similarities) {
        this.similarities = similarities;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
    }

    public String getDisplayName() {
        return display_name;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public boolean isGuide() {
        return guide_status;
    }

    public void setGuideStatus(boolean guide_status) {
        this.guide_status = guide_status;
    }

    public String getUid() {
        return uid;
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

    public void setOtherPrefs(HashMap<String, Boolean> other_prefs) { this.other_prefs = other_prefs; }

    public HashMap<String, String> getLocations() { return locations; }

    public void setLocations(HashMap<String, String> locations) { this.locations = locations; }
}
