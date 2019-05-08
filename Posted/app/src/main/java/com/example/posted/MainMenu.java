package com.example.posted;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posted.dummy.DummyContent;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener, GuideFragment.OnListFragmentInteractionListener ,GuidePage.OnFragmentInteractionListener,View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String display_name;
    private boolean guide_status;
    private String uid;
    private String email;
    private ImageView profilePicture;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    SharedPreferences settings;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get user information (display display_name, email, and profile pic) from Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        if(firebaseAuth.getCurrentUser() != null){
            user = firebaseAuth.getCurrentUser();
            uid = user.getUid();
            //getting preferences from a specified file
            email = user.getEmail();
            //TODO: set profile picture
        }

        // Populate navigation bar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        // Setup navigation drawer and toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        pullInformation();
        updateInformation();
        // Initialize the main main container with the home fragment
        changeFragment(new Home());
    }

    @Override
    public void onResume(){
        super.onResume();

        //Repopulate settings and displayed information from the database
        pullInformation();
        updateInformation();
    }

    private void updateInformation() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.user_name);
        display_name = settings.getString(getString(R.string.display_name), null);
        navUsername.setText(display_name);
        TextView navEmail = (TextView) headerView.findViewById(R.id.user_email);
        navEmail.setText(email);
        profilePicture = headerView.findViewById(R.id.imageView);
        storageRef.child("profileImages/" + user.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.get().load(uri).into(profilePicture);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        profilePicture.setOnClickListener(this);
    }

    public void pullInformation() {
        // Update shared preferences from the preferences on the database (in case the user logged in on another device and changed it there)
        DatabaseReference ref = mDatabase.child("users").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();

                if(dataSnapshot.exists()){
                    Profile user = dataSnapshot.getValue(Profile.class);

                    editor.putString(getString(R.string.display_name), user.getDisplayName());
                    editor.putBoolean(getString(R.string.guide_status), user.isGuide());

                    Set<String> foodEntries = new HashSet<String>();
                    String[] foodCategories = getResources().getStringArray(R.array.food_categories);
                    for(int i = 0; i < foodCategories.length; i++){
                        if(user.food_prefs.get(foodCategories[i])){
                            foodEntries.add(Integer.toString(i+1));
                        }else{
                            foodEntries.remove(Integer.toString(i+1));
                        }
                    }
                    editor.putStringSet(getString(R.string.pref_category_food), foodEntries);

                    Set<String> otherEntries = new HashSet<String>();
                    String[] otherCategories = getResources().getStringArray(R.array.other_categories);
                    for(int i = 0; i < otherCategories.length; i++){
                        if(user.other_prefs.get(otherCategories[i])){
                            otherEntries.add(Integer.toString(i+1));
                        }else{
                            otherEntries.remove(Integer.toString(i+1));
                        }
                    }
                    editor.putStringSet(getString(R.string.pref_category_other), otherEntries);
                    editor.commit();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            changeFragment(new Home());
        }
    }

    // Handles navigation view item clicks
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_locations) {
            changeFragment(new DiscoverLocations());
        } else if (id == R.id.nav_guides) {
            changeFragment(new GuideFragment());
        } else if (id == R.id.nav_chats) {
            changeFragment(new Chat());
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(this, AccountSettings.class));
            overridePendingTransition(0, 0);
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(this, NotificationSettings.class));
            overridePendingTransition(0,0);
        } else if (id == R.id.sign_out){
            firebaseAuth.signOut();
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
            changeFragment(new GuideFragment());
        } else if (uri.toString().equals(getString(R.string.chat))){
            changeFragment(new Chat());
        } else if (uri.toString().equals(getString(R.string.location))){
            changeFragment(Location.newInstance("fg9XJ8kWd6BQqEjjPreU0g"));
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

    @Override
    public void onListFragmentInteraction(Profile item) {
        changeFragment(GuidePage.newInstance(item.display_name));
    }

    @Override
    public void onClick(View v) {
        if(v == profilePicture){
            pickImage();
        }
    }
    public static final int PICK_IMAGE = 1;

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                InputStream stream = this.getContentResolver().openInputStream(data.getData());
                StorageReference imageStorage = storageRef.child("profileImages/" + user.getUid());
                UploadTask uploadTask = imageStorage.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
