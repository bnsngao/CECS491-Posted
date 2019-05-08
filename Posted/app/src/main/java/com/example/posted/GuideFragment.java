package com.example.posted;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RatingBar;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GuideFragment extends Fragment {
    private DatabaseReference mDatabase;
    private MyGuideRecyclerViewAdapter mAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private Profile myProfile;
    private HashMap<String,Boolean> my_food_prefs;
    private HashMap<String,Boolean> my_other_prefs;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GuideFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GuideFragment newInstance(int columnCount) {
        GuideFragment fragment = new GuideFragment();
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_list, container, false);
        // Set the adapter
        mAdapter = new MyGuideRecyclerViewAdapter(DummyContent.ITEMS, mListener);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mAdapter);
        }
        DatabaseReference myProfileRef = mDatabase.child("users").child(currentUser.getUid());
        myProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myProfile = dataSnapshot.getValue(Profile.class);
                my_food_prefs = myProfile.food_prefs;
                my_other_prefs = myProfile.other_prefs;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DummyContent.ITEMS.clear();
        DatabaseReference myRef = mDatabase.child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> userIDS = dataSnapshot.getChildren();
                for(DataSnapshot user:userIDS){
                    Profile p = user.getValue(Profile.class);
                    {
                        if (p.isGuide()&&!p.getUid().equals(currentUser.getUid())){
                            DummyContent.ITEMS.add(p);
                            mAdapter.notifyDataSetChanged();
                            HashMap<String,Boolean> temp_food_prefs = p.food_prefs;
                            HashMap<String,Boolean> temp_other_prefs = p.other_prefs;
                            ArrayList<String> similarities = new ArrayList<>();
                            for (HashMap.Entry<String, Boolean> entry : temp_food_prefs.entrySet()) {
                                for (HashMap.Entry<String, Boolean> entry2 : my_food_prefs.entrySet()) {
                                    if(entry2.getKey().equals(entry.getKey())&&entry2.getValue().equals(entry.getValue())){
                                        //System.out.println(p.display_name+" "+entry.getKey() + " = " + entry.getValue());
                                        similarities.add(entry.getKey());
                                    }
                                }
                            }
                            for (HashMap.Entry<String, Boolean> entry : temp_other_prefs.entrySet()) {
                                for (HashMap.Entry<String, Boolean> entry2 : my_other_prefs.entrySet()) {
                                    if(entry2.getKey().equals(entry.getKey())&&entry2.getValue().equals(entry.getValue())){
                                        //System.out.println(p.display_name+" "+entry.getKey() + " = " + entry.getValue());
                                        similarities.add(entry.getKey());
                                    }
                                }
                            }
                            p.setSimilarities(similarities);
                            //System.out.println(p.display_name+": "+similarities);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        void onListFragmentInteraction(Profile item);
    }
}
