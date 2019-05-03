package com.example.posted;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.Hour;
import com.yelp.fusion.client.models.Open;
import com.yelp.fusion.client.models.SearchResponse;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Location#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Location extends Fragment implements View.OnClickListener{
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 0;
    private OnFragmentInteractionListener mListener;
    private String locationID = "ashoka-the-great-artesia";
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private String apiKey = "R6yVr4Q3RYIwMLnELCLqgoCaQeGsoYXoGgxYZo2jEIurtkAs2uaookblm0J3fzz-7GGKPwDTiZ_N5xoxygiPUIwymxXvppyySCe-f9HUWZVrOR_dwj7wMN5W0-jDXHYx";
    private Business business;
    private Call<Business> call;
    private View view;

    public Location() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Location.
     */
    public static Location newInstance(String locationID) {
        Location fragment = new Location();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_location, container, false);

        // Get user's last known location


        // Update UI text with the Business object.
        try {
            YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
            YelpFusionApi yelpFusionApi = apiFactory.createAPI(apiKey);

            Callback<Business> callback = new Callback<Business>() {
                @Override
                public void onResponse(Call<Business> call, Response<Business> response) {
                    business = response.body();
                    System.out.println(business);
                    // Business name
                    String businessName = business.getName();  // "Ashoka The Great"
                    TextView businessNameView = (TextView) view.findViewById(R.id.location_name);
                    businessNameView.setText(businessName);

                    // Business photo
                    String imageUrl = business.getImageUrl();
                    ImageView imageView = (ImageView) view.findViewById(R.id.location_picture);
                    Picasso.get().load(imageUrl).into(imageView);

                    // Distance
                    double distance = business.getDistance();
                    TextView distanceView = (TextView) view.findViewById(R.id.distance);
                    distanceView.setText(distance + " mi");

                    // Rating
                    float rating = (float) business.getRating();  // 4.0
                    RatingBar ratingBarView = (RatingBar) view.findViewById(R.id.rating);
                    ratingBarView.setRating(rating);

                    // Review Count
                    int reviewCount = business.getReviewCount();
                    TextView reviewCountView = (TextView) view.findViewById(R.id.reviewCount);
                    reviewCountView.setText(reviewCount + " Reviews");

                    // Price
                    String price = business.getPrice();
                    TextView priceView = (TextView) view.findViewById(R.id.price);
                    priceView.setText(price);

                    // Categories
                    ArrayList<Category> categoriesList = business.getCategories();
                    String categories = categoriesList.get(0).getTitle();
                    for (int i =1; i< categoriesList.size(); i++){
                        categories = categories + ", " + categoriesList.get(i).getTitle();
                    }
                    TextView categoriesView = (TextView) view.findViewById(R.id.categories);
                    categoriesView.setText(categories);

                    // Is closed
                    boolean isClosed = business.getIsClosed();
                    TextView closedView = (TextView) view.findViewById(R.id.closed);
                    if(isClosed){
                        closedView.setText("Closed");
                        closedView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorClosed));
                    }else{
                        closedView.setText("Open");
                        closedView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorOpen));
                    }

                    // Hours
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    ArrayList<Hour> hoursList = business.getHours();
                    ArrayList<Open> openList = hoursList.get(0).getOpen();
                    String todaysHours = "";
                    for(int i=0; i < openList.size(); i ++){
                        if((day - 2) == openList.get(i).getDay()){
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                                final Date dateObjStart = sdf.parse(openList.get(i).getStart());
                                final Date dateObjEnd = sdf.parse(openList.get(i).getEnd());
                                String startHour = new SimpleDateFormat("K:mm a").format(dateObjStart);
                                String endHour = new SimpleDateFormat("K:mm a").format(dateObjEnd);
                                if(todaysHours.equals("")){
                                    todaysHours = startHour + " - " + endHour;
                                } else{
                                    todaysHours = todaysHours + ", " + startHour + " - " + endHour;
                                }
                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    TextView hoursView = (TextView) view.findViewById(R.id.hours);
                    hoursView.setText(todaysHours);

                }
                @Override
                public void onFailure(Call<Business> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                    System.out.println("on Failure");
                }
            };

            call = yelpFusionApi.getBusiness(locationID);
            call.enqueue(callback);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        call.cancel();
        mListener = null;
    }

}
