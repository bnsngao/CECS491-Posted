package com.example.posted;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatList extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Private view
    private View chatView;

    private int temp = 0;

    private float totalRating;

    private float retrievedRating;

    // Recycler view for displaying info
    private RecyclerView chatList;

    // Database references for retrieving data
    private DatabaseReference chatListReference, // reference for message lists
            usersReference; // messages for users

    // Firebase auth for user id
    private FirebaseAuth mAuth;

    // For saving current user ID
    private String currentUID;

    public ChatList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatList.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatList newInstance(String param1, String param2) {
        ChatList fragment = new ChatList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        chatView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get current userID
        currentUID = mAuth.getCurrentUser().getUid();


        // Initialize database references for current user
        chatListReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUID);
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        // Create RecyclerView and set layout manager
        chatList = (RecyclerView) chatView.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        return chatView;
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

    }

    

    @Override
    public void onStart() {
        super.onStart();

        // Create recycler options
        // This contains our query and will be passed into the adapter
        FirebaseRecyclerOptions<Profile> options =
                new FirebaseRecyclerOptions.Builder<Profile>()
                .setQuery(chatListReference, Profile.class)
                .build();

        // create recycler adapter
        FirebaseRecyclerAdapter<Profile, ChatListViewHolder> adapter =
                new FirebaseRecyclerAdapter<Profile, ChatListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatListViewHolder holder, int position, @NonNull Profile model) {
                        // Iterate through user IDs attached to current user

                        // Get userID at current position
                        final String userIDs = getRef(position).getKey();

                        usersReference.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                temp = 0; // reset tracker
                                retrievedRating = 0; // reset total rating

                                // get total ratings and average them;
                                for(DataSnapshot item_snapshot:dataSnapshot.child("ratings").getChildren()) {
                                    retrievedRating += Float.parseFloat(item_snapshot.getValue().toString());
                                    temp++;
                                }

                                if (temp != 0) {
                                    totalRating = retrievedRating / temp;
                                    usersReference.child(userIDs).child("ratings").child("total_rating").setValue(totalRating);
                                }

                                if (dataSnapshot.exists()) {
                                    final String retrievedProfilePhoto = dataSnapshot.child("profile_photo").getValue().toString();
                                    final String retrievedDisplayName = dataSnapshot.child("display_name").getValue().toString();

                                    Picasso.get().load(retrievedProfilePhoto).into(holder.profilePhoto);
                                    holder.displayName.setText(retrievedDisplayName);
                                    holder.profileRating.setRating(totalRating);
                                    holder.profileRating.setIsIndicator(true);

                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            changeFragment(new Chat().newInstance(userIDs));
                                        }
                                    });
                                }
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
                    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        // Create view to display profiles and return it
                        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_guide, viewGroup, false);
                        return new ChatListViewHolder(v);
                    }
                };

        // Set adapter and start listening
        chatList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePhoto;
        TextView displayName;
        RatingBar profileRating;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = itemView.findViewById(R.id.guideProfileImage);
            displayName = itemView.findViewById(R.id.guideUsername);
            profileRating = itemView.findViewById(R.id.guideRatingBar);
        }
    }


    // Changes the fragment being displayed in the main page
    // Call with changeFragment(new FragmentConstructor());
    public void changeFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment).addToBackStack(null).commit();
    }
}
