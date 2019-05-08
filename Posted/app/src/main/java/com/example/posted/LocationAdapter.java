//package com.example.posted;
//
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import com.example.posted.GuideFragment.OnListFragmentInteractionListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
//
//    private final List<LocationItem> mValues;
//    private final OnListFragmentInteractionListener mListener;
//
//    public LocationAdapter(List<LocationItem> items, OnListFragmentInteractionListener listener) {
//        mValues = items;
//        mListener = listener;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.fragment_guide, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = mValues.get(position);
//        holder.mContentView.setText(mValues.get(position).display_name);
//        holder.mRating.setNumStars(mValues.get(position).rating);
//        Picasso.get().load(mValues.get(position).getProfile_photo()).into(holder.mImageView);
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (null != mListener) {
//                    // Notify the active callbacks interface (the activity, if the
//                    // fragment is attached to one) that an item has been selected.
//                    mListener.onListFragmentInteraction(holder.mItem);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return mValues.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public final View mView;
//        public final TextView mContentView;
//        public Profile mItem;
//        public RatingBar mRating;
//        public ImageView mImageView;
//        public ViewHolder(View view) {
//            super(view);
//            mView = view;
//            mContentView = (TextView) view.findViewById(R.id.guideUsername);
//            mRating = view.findViewById(R.id.guideRatingBar);
//            mImageView = view.findViewById(R.id.guideProfileImage);
//        }
//
//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
//    }
//}
