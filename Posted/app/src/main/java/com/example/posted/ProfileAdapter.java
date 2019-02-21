package com.example.posted;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.myViewHolder> {

    Context mContext;
    List<ProfileItem> profileData;

    public ProfileAdapter(Context mContext, List<ProfileItem> profileData) {
        this.mContext = mContext;
        this.profileData = profileData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.profile_item_view, viewGroup, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.profilePhoto.setImageResource(profileData.get(position).getProfilePhoto());
        holder.profileName.setText(profileData.get(position).getProfileName());
        holder.profileRating.setNumStars(profileData.get(position).ratingNumber);
    }

    @Override
    public int getItemCount() {
        return profileData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePhoto;
        TextView profileName;
        RatingBar profileRating;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePhoto = itemView.findViewById(R.id.profile_picture);
            profileName = itemView.findViewById(R.id.txt_username);
            profileRating = itemView.findViewById(R.id.ratingBar);
        }
    }
}
