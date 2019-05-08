package com.example.posted;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class Profile {
    public String display_name;
    public boolean guide_status;
    public int rating;
    public String profile_photo;
    public String uid;
    public HashMap<String, Boolean> food_prefs;
    public HashMap<String, Boolean> other_prefs;

    public Profile() {
    }

    public Profile(String display_name, String uid, int rating, String profile_photo, boolean guide_status, HashMap<String, Boolean> food_prefs, HashMap<String, Boolean> other_prefs) {
        this.display_name = display_name;
        this.uid = uid;
        this.rating = rating;
        this.profile_photo = profile_photo;
        this.guide_status = guide_status;
        this.food_prefs = food_prefs;
        this.other_prefs = other_prefs;
        this.profile_photo = profile_photo;
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

    public void setOtherPrefs(HashMap<String, Boolean> other_prefs) {
        this.other_prefs = other_prefs;
    }
}
