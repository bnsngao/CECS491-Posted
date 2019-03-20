package com.example.posted;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import static java.security.AccessController.getContext;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            //TODO: handle redirect to chats list
            Toast.makeText(getApplicationContext(), "Chats not yet implemented", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            changeFragment(new Settings());
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
            //TODO: handle redirect to chats list
            Toast.makeText(getApplicationContext(), "Chat not yet implemented", Toast.LENGTH_SHORT).show();
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
