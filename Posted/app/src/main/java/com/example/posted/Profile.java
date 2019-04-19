package com.example.posted;

import java.util.HashMap;

class Profile {
    public String display_name;
    public boolean guide_status;
    public HashMap<String, Boolean> food_prefs;
    public HashMap<String, Boolean> other_prefs;

    public Profile() {
    }

    public Profile(String display_name, boolean guide_status, HashMap<String, Boolean> food_prefs, HashMap<String, Boolean> other_prefs) {
        this.display_name = display_name;
        this.guide_status = guide_status;
        this.food_prefs = food_prefs;
        this.other_prefs = other_prefs;
    }

    public String getDisplayName() {
        return display_name;
    }

    public void setDisplayName(String display_name) {
        this.display_name = display_name;
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
