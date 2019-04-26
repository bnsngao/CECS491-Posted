package com.example.posted;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: AccountSettings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">AccountSettings
 * API Guide</a> for more information on developing a AccountSettings UI.
 */
public class AccountSettings extends AppCompatPreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private String uid;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences settings, String key){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(key == getString(R.string.display_name)){
            String display_name = settings.getString(getString(R.string.display_name), null);
            mDatabase.child("users").child(uid).child("display_name").setValue(display_name);
            Toast.makeText(getApplicationContext(), "Display name changed", Toast.LENGTH_SHORT).show();
        } else if(key == getString(R.string.guide_status)){
            Toast.makeText(getApplicationContext(), "Guide status changed", Toast.LENGTH_SHORT).show();
            Boolean guide_status = settings.getBoolean(getString(R.string.guide_status), false);
            mDatabase.child("users").child(uid).child("guide_status").setValue(guide_status);
            //Toast.makeText(getApplicationContext(), "Guide status changed", Toast.LENGTH_SHORT).show();
        } else if(key == getString(R.string.pref_category_food)){
            Set<String> entries = settings.getStringSet(getString(R.string.pref_category_food), new HashSet<String>());
            String[] categories = getResources().getStringArray(R.array.food_categories);
            for(int i = 0; i < categories.length; i++) {
                boolean selected = false;
                if(entries.contains(Integer.toString(i+1))){
                    selected = true;
                }
                mDatabase.child("users").child(uid).child("food_prefs").child(categories[i]).setValue(selected);
            }
            Toast.makeText(getApplicationContext(), "Food prefs changed", Toast.LENGTH_SHORT).show();
        } else if(key == getString(R.string.pref_category_other)){
            Set<String> entries = settings.getStringSet(getString(R.string.pref_category_other), new HashSet<String>());
            String[] categories = getResources().getStringArray(R.array.other_categories);
            for(int i = 0; i < categories.length; i++) {
                boolean selected = false;
                if(entries.contains(Integer.toString(i+1))){
                    selected = true;
                }
                mDatabase.child("users").child(uid).child("other_prefs").child(categories[i]).setValue(selected);
            }
            Toast.makeText(getApplicationContext(), "Other prefs changed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Get user information (display display_name, email, and profile pic) from Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(firebaseAuth.getCurrentUser() != null){
            user = firebaseAuth.getCurrentUser();
            uid = user.getUid();
        }

        //Initialize shared preference listeners to update the database anytime a preference is changed
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_preferences);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || PreferencesFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows suggestion preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_preferences);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), AccountSettings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
