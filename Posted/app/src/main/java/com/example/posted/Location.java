package com.example.posted;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.yelp.fusion.client.models.Category;
import com.yelp.fusion.client.models.Hour;
import com.yelp.fusion.client.models.Open;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private static String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener mListener;
    private String locationID;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private String apiKey = "R6yVr4Q3RYIwMLnELCLqgoCaQeGsoYXoGgxYZo2jEIurtkAs2uaookblm0J3fzz-7GGKPwDTiZ_N5xoxygiPUIwymxXvppyySCe-f9HUWZVrOR_dwj7wMN5W0-jDXHYx";
    private Business business;
    double longitude;
    double latitude;
    private Call<Business> call;
    private View view;
    private DatabaseReference guideListReference, // reference for guides
            usersReference; // info for guides
    private RecyclerView guideList;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    private DatabaseReference mDatabase;
    private Profile userSnapshot;
    private Button googleMapsButton;
    private Button yelpButton;

    public Location() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Location.
     */
    public static Location newInstance(String locID) {
        Location fragment = new Location();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, locID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_location_page, container, false);
        googleMapsButton = view.findViewById(R.id.mapsButton);
        googleMapsButton.setOnClickListener(this);
        yelpButton = view.findViewById(R.id.yelpButton);
        yelpButton.setOnClickListener(this);
        guideListReference = FirebaseDatabase.getInstance().getReference().child("Locations").child(locationID).child("Guides");
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");
        guideList = (RecyclerView) view.findViewById(R.id.guide_list);
        guideList.setLayoutManager(new LinearLayoutManager(getContext()));


        // Update UI text with the Business object.
        try {
            YelpFusionApiFactory apiFactory = new YelpFusionApiFactory();
            YelpFusionApi yelpFusionApi = apiFactory.createAPI(apiKey);

            Callback<Business> callback = new Callback<Business>() {
                @Override
                public void onResponse(Call<Business> call, Response<Business> response) {
                    business = response.body();
                    String businessId = business.getId();
                    System.out.println(businessId);

                    // Business coordinates
                    longitude = business.getCoordinates().getLongitude();
                    latitude = business.getCoordinates().getLatitude();

                    // Business name
                    String businessName = business.getName();
                    TextView businessNameView = (TextView) view.findViewById(R.id.location_name);
                    businessNameView.setText(businessName);

                    // Business photo
                    String imageUrl = business.getImageUrl();
                    ImageView imageView = (ImageView) view.findViewById(R.id.location_picture);
                    Picasso.get().load(imageUrl).into(imageView);

                    // Rating
                    float rating = (float) business.getRating();
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


                    // Hours
                    Calendar calendar = Calendar.getInstance();
                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                    ArrayList<Hour> hoursList = business.getHours();
                    ArrayList<Open> openList = hoursList.get(0).getOpen();
                    String todaysHours = "";
                    for(int i=0; i < openList.size(); i ++){
                        if((day - 2) == openList.get(i).getDay()){
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
                                Date dateObjStart = sdf.parse(openList.get(i).getStart());
                                Date dateObjEnd = sdf.parse(openList.get(i).getEnd());
                                String startHour = new SimpleDateFormat("hh:mm a").format(dateObjStart);
                                String endHour = new SimpleDateFormat("hh:mm a").format(dateObjEnd);
                                if(todaysHours.equals("")){
                                    todaysHours = startHour + " - " + endHour;
                                } else{
                                    todaysHours = todaysHours + ", " + startHour + " - " + endHour;
                                }

                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.MONTH, 0);
                                cal.set(Calendar.YEAR, 1970);
                                cal.set(Calendar.DAY_OF_YEAR, 1);
                                Date currDate = cal.getTime();
                                if(openList.get(i).getIsOvernight() || endHour.equals("12:00 AM")){
                                    Calendar calTemp = Calendar.getInstance();
                                    calTemp.setTime(dateObjEnd);
                                    calTemp.set(Calendar.DAY_OF_YEAR, 2);
                                    dateObjEnd = calTemp.getTime();
                                }
                                TextView closedView = (TextView) view.findViewById(R.id.closed);
                                if((dateObjStart.before(currDate)) && (currDate.before(dateObjEnd))) {
                                    closedView.setText("Open");
                                    closedView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorOpen));
                                }else{
                                    closedView.setText("Closed");
                                    closedView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorClosed));
                                }

                            } catch (final ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    TextView hoursView = (TextView) view.findViewById(R.id.hours);
                    hoursView.setText(todaysHours);

                    ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressLocationPage);
                    progressBar.setVisibility(View.GONE);
                    ratingBarView.setVisibility(View.VISIBLE);
                    TextView spacer = (TextView) view.findViewById(R.id.spacer);
                    spacer.setVisibility(View.VISIBLE);
                    TextView spacer2 = (TextView) view.findViewById(R.id.spacer2);
                    spacer2.setVisibility(View.VISIBLE);
                    yelpButton.setVisibility(View.VISIBLE);
                    googleMapsButton.setVisibility(View.VISIBLE);
                    Switch s = (Switch) view.findViewById(R.id.visited);
                    s.setVisibility(View.VISIBLE);
                    View dividerView = (View) view.findViewById(R.id.guideDivider);
                    dividerView.setVisibility(View.VISIBLE);
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.guide_list);
                    recyclerView.setVisibility(View.VISIBLE);

                }
                @Override
                public void onFailure(Call<Business> call, Throwable t) {
                    // HTTP error happened, do something to handle it.
                    System.out.println("on Failure");
                }
            };

            call = yelpFusionApi.getBusiness(locationID);
            call.enqueue(callback);

            // Get user information (display display_name, email, and profile pic) from Firebase
            firebaseAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            if(firebaseAuth.getCurrentUser() != null){
                user = firebaseAuth.getCurrentUser();
                uid = user.getUid();
            }

            DatabaseReference ref = mDatabase.child("users").child(uid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        userSnapshot = dataSnapshot.getValue(Profile.class);
                        try{
                            boolean visited = userSnapshot.getLocations().containsKey(locationID);
                            Switch s = (Switch) view.findViewById(R.id.visited);
                            s.setChecked(visited);
                        } catch(NullPointerException e){
                            Switch s = (Switch) view.findViewById(R.id.visited);
                            s.setChecked(false);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            Switch s = (Switch) view.findViewById(R.id.visited);
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mDatabase.child("users").child(uid).child("locations").child(locationID).setValue("1");
                        if(userSnapshot.isGuide()){
                            mDatabase.child("Locations").child(locationID).child("Guides").child(uid).child("guide_status").setValue(true);
                        }
                    } else {
                        mDatabase.child("users").child(uid).child("locations").child(locationID).removeValue();
                        mDatabase.child("Locations").child(locationID).child("Guides").child(uid).child("guide_status").removeValue();
                    }
                }
            });


        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v){
        if (v == googleMapsButton){
            changeFragment(new GoogleMaps().newInstance(latitude, longitude));
        } else if (v == yelpButton){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(business.getUrl()));
            startActivity(i);
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        // Create recycler options
        // This contains our query and will be passed into the adapter
        System.out.println(guideListReference);

        FirebaseRecyclerOptions<Profile> options =
                new FirebaseRecyclerOptions.Builder<Profile>()
                        .setQuery(guideListReference, Profile.class)
                        .build();
        System.out.println(options.getSnapshots());

        // create recycler adapter
        FirebaseRecyclerAdapter<Profile, GuideListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Profile, GuideListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final GuideListViewHolder holder, int position, @NonNull Profile model) {

                        // Get userID at current position
                        final String userIDs = getRef(position).getKey();
                        System.out.println(userIDs);
                        usersReference.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // TODO add datasnapshot for profile image
                                final Profile user = dataSnapshot.getValue(Profile.class);
                                final String retrievedProfilePhoto = dataSnapshot.child("profile_photo").getValue().toString();
                                final String retrievedDisplayName = dataSnapshot.child("display_name").getValue().toString();
                                Picasso.get().load(retrievedProfilePhoto).into(holder.profilePhoto);
                                holder.displayName.setText(retrievedDisplayName);
                                DatabaseReference totalRating = mDatabase.child("users").child(user.getUid()).child("ratings").child("total_rating");
                                totalRating.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getValue()!= null){
                                            holder.guideRating.setRating(Float.parseFloat(dataSnapshot.getValue().toString()));
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changeFragment(new GuidePage().newInstance(user));
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // display error
                                holder.displayName.setText(databaseError.toString());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public GuideListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        // Create view to display profiles and return it
                        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_guide, viewGroup, false);
                        return new GuideListViewHolder(v);
                    }
                };

        // Set adapter and start listening
        guideList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class GuideListViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePhoto;
        TextView displayName;
        RatingBar guideRating;

        public GuideListViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.guideProfileImage);
            displayName = itemView.findViewById(R.id.guideUsername);
            guideRating = itemView.findViewById(R.id.guideRatingBar);
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

    // Changes the fragment being displayed in the main page
    // Call with changeFragment(new FragmentConstructor());
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment).addToBackStack(null).commit();
    }
}
