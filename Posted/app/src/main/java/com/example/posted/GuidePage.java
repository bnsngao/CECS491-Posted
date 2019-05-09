package com.example.posted;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GuidePage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuidePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuidePage extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String ARG_PARAM1 = "param1";
    private static Profile profile1;
    private TextView guide_name;
    private ImageView guide_image;
    private OnFragmentInteractionListener mListener2;
    private View view;
    private String uid;
    private DatabaseReference mDatabase;
    private LocationAdapter mAdapter;
    private int mColumnCount = 1;
    private LocationFragment.OnListFragmentInteractionListener mListener;
    public static final List<LocationItem> LOCATION_ITEMS = new ArrayList<LocationItem>();
    private String apiKey = "R6yVr4Q3RYIwMLnELCLqgoCaQeGsoYXoGgxYZo2jEIurtkAs2uaookblm0J3fzz-7GGKPwDTiZ_N5xoxygiPUIwymxXvppyySCe-f9HUWZVrOR_dwj7wMN5W0-jDXHYx";
    private Call<Business> call;


    // TODO: Rename and change types of parameters
    private String mParam1;

    private Button chat_button;


    public GuidePage() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GuidePage newInstance(Profile profile) {
        GuidePage fragment = new GuidePage();
        Bundle args = new Bundle();
        profile1 = profile;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_guide_page, container, false);
        guide_name = view.findViewById(R.id.guide_name);
        guide_name.setText(profile1.getDisplayName());
        guide_image = view.findViewById(R.id.guideProfileImage);
        Picasso.get().load(profile1.getProfile_photo()).into(guide_image);
        chat_button = view.findViewById(R.id.chat_button);
        chat_button.setOnClickListener(this);
        uid = profile1.getUid();


        // Get user information (display display_name, email, and profile pic) from Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();


        // Set the adapter
        mAdapter = new LocationAdapter(LOCATION_ITEMS, mListener);
        View locationView = view.findViewById(R.id.guide_location_list);
        if (locationView instanceof RecyclerView) {
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


        DatabaseReference ref = mDatabase.child("users").child(uid).child("locations");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> locationIDs = dataSnapshot.getChildren();

                    for (DataSnapshot location : locationIDs) {
                        String locationID = location.getKey();

                        try {
                            YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
                            YelpFusionApi yelpFusionApi = apiFactory.createAPI(apiKey);

                            Callback<Business> callback = new Callback<Business>() {
                                @Override
                                public void onResponse(Call<Business> call, Response<Business> response) {
                                    Business business = response.body();

                                    final String businessId = business.getId();

                                    // Business name
                                    final String businessName = business.getName();
                                    System.out.println(businessName);

                                    // Business photo
                                    final String imageUrl = business.getImageUrl();

                                    // Rating
                                    final float rating = (float) business.getRating();
                                    LocationItem l = new LocationItem(imageUrl, businessName, businessId, rating);


                                    LOCATION_ITEMS.add(l);
                                    System.out.println(LOCATION_ITEMS);
                                    mAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onFailure(Call<Business> call, Throwable t) {
                                    // HTTP error happened, do something to handle it.
                                    System.out.println("on Failure");
                                }
                            };

                            call = yelpFusionApi.getBusiness(locationID);
                            call.enqueue(callback);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("db error");
            }
        });


//            Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
//            call.enqueue(callback);


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener2 != null) {
            mListener2.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragment.OnListFragmentInteractionListener) {
            mListener = (LocationFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener2 = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == chat_button) {
            changeFragment(new Chat().newInstance(profile1.getUid()));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // Changes the fragment being displayed in the main page
    // Call with changeFragment(new FragmentConstructor());
    public void changeFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment).addToBackStack(null).commit();
    }
}
