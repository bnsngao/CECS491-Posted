package com.example.posted;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posted.dummy.DummyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.Hour;
import com.yelp.fusion.client.models.Open;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LocationFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private LocationAdapter mAdapter;
    String userID;
    private View view;
    private String apiKey = "R6yVr4Q3RYIwMLnELCLqgoCaQeGsoYXoGgxYZo2jEIurtkAs2uaookblm0J3fzz-7GGKPwDTiZ_N5xoxygiPUIwymxXvppyySCe-f9HUWZVrOR_dwj7wMN5W0-jDXHYx";
    private Call<Business> call;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public static final List<LocationItem> LOCATION_ITEMS = new ArrayList<LocationItem>();
    private FirebaseUser user;
    private String uid;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LocationFragment newInstance(int columnCount) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = firebaseAuth.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location_list, container, false);
        // Get user information (display display_name, email, and profile pic) from Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(firebaseAuth.getCurrentUser() != null){
            user = firebaseAuth.getCurrentUser();
            uid = user.getUid();
        }
        
        // Set the adapter
        mAdapter = new LocationAdapter(LOCATION_ITEMS, mListener);
        View locationView = view.findViewById(R.id.locations_list);
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

                        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressLocationList);
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
            params.put("limit", "50");

            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
            call.enqueue(callback);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String locationID);
    }
}
