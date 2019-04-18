package com.example.posted;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String display_name;
    private String uid;
    private String email;
    private DatabaseReference mDatabase;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user information (display display_name, email, and profile pic) from Firebase and populate the navigation bar with it
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        if(firebaseAuth.getCurrentUser() != null){
            user = firebaseAuth.getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            uid = user.getUid();
            //getting preferences from a specified file
            display_name = settings.getString(getString(R.string.display_name), null);
            email = user.getEmail();
            //TODO: set profile picture

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.user_name);
            navUsername.setText(display_name);
            TextView navEmail = (TextView) headerView.findViewById(R.id.user_email);
            navEmail.setText(email);
        }

        //Initialize shared preference listeners for display display_name and profile picture
        SharedPreferences.OnSharedPreferenceChangeListener listener;
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences settings, String key){
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);

                if(key == getString(R.string.display_name)){
                    display_name = settings.getString(getString(R.string.display_name), null);
                    TextView navUsername = (TextView) headerView.findViewById(R.id.user_name);
                    navUsername.setText(display_name);
                    mDatabase.child("users").child(uid).child("display_name").setValue(display_name);
                    Toast.makeText(getApplicationContext(), "Display name changed", Toast.LENGTH_SHORT).show();
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
                }
            }
        };
        settings.registerOnSharedPreferenceChangeListener(listener);

        // Setup navigation drawer and toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize the main main container with the home fragment
        changeFragment(new Home());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Handles navigation view item clicks
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_locations) {
            changeFragment(new DiscoverLocations());
        } else if (id == R.id.nav_guides) {
            changeFragment(new DiscoverGuides());
        } else if (id == R.id.nav_chats) {
            changeFragment(new Chat());
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountSettings.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(this, NotificationSettings.class));
            overridePendingTransition(0,0);
        } else if (id == R.id.sign_out){
            firebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Login.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handles onclicks for buttons in fragments
    @Override
    public void onFragmentInteraction(Uri uri){
        if(uri.toString().equals(getString(R.string.discover_locations))){
            changeFragment(new DiscoverLocations());
        } else if (uri.toString().equals(getString(R.string.discover_guides))){
            changeFragment(new DiscoverGuides());
        } else if (uri.toString().equals(getString(R.string.chat))){
            changeFragment(new Chat());
        } else if (uri.toString().equals(getString(R.string.location))){
            changeFragment(new Location());
        } else if (uri.toString().equals(getString(R.string.guide))){
            changeFragment(new Guide());
        }
    }

    // Changes the fragment being displayed in the main page
    // Call with changeFragment(new FragmentConstructor());
    @Override
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment);
        ft.commit();
    }
}
