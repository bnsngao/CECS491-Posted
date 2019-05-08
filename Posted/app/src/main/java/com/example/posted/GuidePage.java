package com.example.posted;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;


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
    private static Profile profile1;
    private TextView guide_name;
    private ImageView guide_image;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference locationListReference;

    // TODO: Rename and change types of parameters

    private Button chat_button;

    private OnFragmentInteractionListener mListener;

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
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_page, container,false);
        guide_name = view.findViewById(R.id.guide_name);
        guide_name.setText(profile1.getDisplayName());
        guide_image = view.findViewById(R.id.guideProfileImage);
        Picasso.get().load(profile1.getProfile_photo()).into(guide_image);
        chat_button = view.findViewById(R.id.chat_button);
        chat_button.setOnClickListener(this);
        locationListReference = FirebaseDatabase.getInstance().getReference().child("Locations");
        if(user.getUid().equals(profile1.getUid())){
            chat_button.setVisibility(view.GONE);
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Create recycler options
//        // This contains our query and will be passed into the adapter
//        FirebaseRecyclerOptions<LocationItem> options =
//                new FirebaseRecyclerOptions.Builder<LocationItem>()
//                        .setQuery(locationListReference, LocationItem.class)
//                        .build();
//
//        // create recycler adapter
//        FirebaseRecyclerAdapter<Profile, Location.GuideListViewHolder> adapter =
//                new FirebaseRecyclerAdapter<Profile, Location.GuideListViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull final Location.GuideListViewHolder holder, int position, @NonNull Profile model) {
//                        // Iterate through user IDs attached to current user
//
//                        // Get userID at current position
//                        final String userIDs = getRef(position).getKey();
//
//                        usersReference.child(userIDs).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                // TODO add datasnapshot for profile image
//                                final String retrievedProfilePhoto = dataSnapshot.child("profile_photo").getValue().toString();
//                                final String retrievedDisplayName = dataSnapshot.child("display_name").getValue().toString();
//                                Picasso.get().load(retrievedProfilePhoto).into(holder.profilePhoto);
//                                holder.displayName.setText(retrievedDisplayName);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//                                // display error
//                                holder.displayName.setText(databaseError.toString());
//                            }
//                        });
//                    }
//
//                    @NonNull
//                    @Override
//                    public Location.GuideListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                        // Create view to display profiles and return it
//                        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_guide, viewGroup, false);
//                        return new Location.GuideListViewHolder(v);
//                    }
//                };
//
//        // Set adapter and start listening
//        guideList.setAdapter(adapter);
//        adapter.startListening();
//    }

    // Changes the fragment being displayed in the main page
    // Call with changeFragment(new FragmentConstructor());
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment).addToBackStack(null).commit();
    }

    public static class LocationListViewHolder extends RecyclerView.ViewHolder {
        ImageView locationPhoto;
        TextView locationName;
        RatingBar locationRating;

        public LocationListViewHolder(@NonNull View itemView) {
            super(itemView);

            locationPhoto = itemView.findViewById(R.id.locationProfileImage);
            locationName = itemView.findViewById(R.id.location_name);
            locationRating = itemView.findViewById(R.id.locationRatingBar);

        }
    }

}
