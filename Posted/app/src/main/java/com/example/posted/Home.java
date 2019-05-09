package com.example.posted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener2;
    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    private DatabaseReference mDatabase;
    private LocationAdapter mAdapter;
    private int mColumnCount = 1;
    private LocationFragment.OnListFragmentInteractionListener mListener;
    public static final List<LocationItem> LOCATION_ITEMS = new ArrayList<LocationItem>();
    private String apiKey = "R6yVr4Q3RYIwMLnELCLqgoCaQeGsoYXoGgxYZo2jEIurtkAs2uaookblm0J3fzz-7GGKPwDTiZ_N5xoxygiPUIwymxXvppyySCe-f9HUWZVrOR_dwj7wMN5W0-jDXHYx";


    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Home.
     */
    public static Home newInstance() {
        Home fragment = new Home();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        uid = firebaseAuth.getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        // Setup button listeners
        Button btn_locations = (Button) view.findViewById(R.id.button_discover_locations);
        btn_locations.setOnClickListener(this);
        Button btn_guides = (Button) view.findViewById(R.id.button_discover_guides);
        btn_guides.setOnClickListener(this);
        Button btn_chat = (Button) view.findViewById(R.id.button_chat);
        btn_chat.setOnClickListener(this);

        // Get user information (display display_name, email, and profile pic) from Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser() != null){
            user = firebaseAuth.getCurrentUser();
            uid = user.getUid();
        }

        // Set the adapter
        mAdapter = new LocationAdapter(LOCATION_ITEMS, mListener);
        View locationView = view.findViewById(R.id.home_list);
        if ( locationView instanceof RecyclerView) {
            Context context = locationView.getContext();
            RecyclerView recyclerView = (RecyclerView) locationView;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mAdapter);
        }
        LOCATION_ITEMS.clear();

        try {
            YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
            YelpFusionApi yelpFusionApi = apiFactory.createAPI(apiKey);

            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    SearchResponse searchResponse = response.body();
                    ArrayList<Business> businesses = searchResponse.getBusinesses();

                    for(Business business : businesses){
                        final String businessId = business.getId();

                        // Business name
                        final String businessName = business.getName();

                        // Business photo
                        final String imageUrl = business.getImageUrl();

                        // Rating
                        final float rating = (float) business.getRating();
                        LocationItem l = new LocationItem(imageUrl,businessName,businessId,rating);


                        DatabaseReference ref = mDatabase.child("Locations");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                System.out.println(dataSnapshot);
                                if(dataSnapshot.exists()){
                                    if(!dataSnapshot.hasChild(businessId)){
                                        mDatabase.child("Locations").child(businessId).child("locationName").setValue(businessName);
                                        mDatabase.child("Locations").child(businessId).child("locationID").setValue(businessId);
                                    }
                                    mDatabase.child("Locations").child(businessId).child("locationPhoto").setValue(imageUrl);
                                    mDatabase.child("Locations").child(businessId).child("rating").setValue(rating);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                        LOCATION_ITEMS.add(l);
                        mAdapter.notifyDataSetChanged();

                        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarHome);
                        progressBar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                    System.out.println("on Failure");
                }
            };

            Map<String, String> params = new HashMap<>();
            String categories = "";
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            Set<String> food_entries = settings.getStringSet(getString(R.string.pref_category_food), new HashSet<String>());
            Set<String> other_entries = settings.getStringSet(getString(R.string.pref_category_other), new HashSet<String>());

            String[] food_categories_yelp = getResources().getStringArray(R.array.food_categories_yelp);
            String[] other_categories_yelp = getResources().getStringArray(R.array.other_categories_yelp);

            for(String item : food_entries){
                if(categories.equals("")){
                    categories = food_categories_yelp[Integer.parseInt(item)-1];
                }else{
                    categories = categories + "," + food_categories_yelp[Integer.parseInt(item)-1];
                }
            }
            for(String item : other_entries){
                if(categories.equals("")){
                    categories = other_categories_yelp[Integer.parseInt(item)-1];
                }else{
                    categories = categories + "," + other_categories_yelp[Integer.parseInt(item)-1];
                }
            }

            // general params
            params.put("categories", categories);
            params.put("location", "Long Beach");
            params.put("limit", "3");

            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
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
            case R.id.button_discover_locations:
                //startActivity(new Intent(getContext(),Rate.class));
                mListener2.onFragmentInteraction(Uri.parse(getString(R.string.discover_locations)));
                break;
            case R.id.button_discover_guides:
                mListener2.onFragmentInteraction(Uri.parse(getString(R.string.discover_guides)));
                break;
            case R.id.button_chat:
                mListener2.onFragmentInteraction(Uri.parse(getString(R.string.chat)));
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragment.OnListFragmentInteractionListener) {
            mListener = (LocationFragment.OnListFragmentInteractionListener) context;
            mListener2 = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
        mListener2 = null;
    }

}
