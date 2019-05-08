package com.example.posted;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMaps extends Fragment{

    private static double lati = 0;
    private static double longi = 0;

    //pass in coordinates that is retrieved from the Location.java file
    public static GoogleMaps newInstance(double double1, double double2){
        GoogleMaps fragment = new GoogleMaps();
        Bundle args = new Bundle();
        lati = double1;
        longi = double2;
        fragment.setArguments(args);
        return fragment;
    }

    public GoogleMaps(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        double coLat = lati;
        double coLong = longi;

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(coLat, coLong, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        //convert coordinates into address
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        //concatenate pieces into a full address
        String fullAddress = (address + ", " + city + ", " + state + " " + postalCode + " " + country + " ");

        //Searching for a specific address will display a pin at that location.
        //Adding a lati/lng to the intent URI will bias the results towards a particular area:
        Uri gmmIntentUri = Uri.parse("geo:"+coLat+", "+coLong+"?q="+fullAddress);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
}
