package com.example.posted.dummy;

import com.example.posted.Profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent extends Profile {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Profile> ITEMS = new ArrayList<Profile>();

    /**
     * A map of sample (dummy) items, by ID.
     */
//    public static final Map<String, ProfileItem> ITEM_MAP = new HashMap<String, ProfileItem>();

    private static void addItem(Profile item) {
        ITEMS.add(item);
//        ITEM_MAP.put(item.id, item);
    }

    private static Profile createProfileItem(String display_name, int rating, int profile_photo, boolean guide_status, HashMap<String, Boolean> food_prefs, HashMap<String, Boolean> other_prefs) {
        return new Profile(display_name,rating,profile_photo,guide_status,food_prefs,other_prefs);
    }

}
