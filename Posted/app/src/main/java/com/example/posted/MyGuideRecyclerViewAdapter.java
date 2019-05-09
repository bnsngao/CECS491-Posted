package com.example.posted;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.posted.GuideFragment.OnListFragmentInteractionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyGuideRecyclerViewAdapter extends RecyclerView.Adapter<MyGuideRecyclerViewAdapter.ViewHolder> {

    private final List<Profile> mValues;
    private final OnListFragmentInteractionListener mListener;
    private DatabaseReference mDatabase;

    public MyGuideRecyclerViewAdapter(List<Profile> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_guide, parent, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).display_name);
//        if(mValues.get(position).getSimilarities().isEmpty()){
//            holder.mSimilarity.setText("0");
//        }
//        else {
//            holder.mSimilarity.setText(Integer.toString(mValues.get(position).getSimilarities().size()));
//        }
        DatabaseReference totalRating = mDatabase.child("users").child(mValues.get(position).getUid()).child("ratings").child("total_rating");
        totalRating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!= null){
                    holder.mRating.setRating(Float.parseFloat(dataSnapshot.getValue().toString()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.mRating.setRating(mValues.get(position).rating);
        Picasso.get().load(mValues.get(position).getProfile_photo()).into(holder.mImageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final TextView mSimilarity;
        public Profile mItem;
        public RatingBar mRating;
        public ImageView mImageView;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.guideUsername);
            mSimilarity = view.findViewById(R.id.guideSimilarity);
            mRating = view.findViewById(R.id.guideRatingBar);
            mImageView = view.findViewById(R.id.guideProfileImage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}