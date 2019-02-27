package com.example.posted;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.myViewHolder> {

    Context mContext;
    List<LocationItem> locationData;

    public LocationAdapter(Context mContext, List<LocationItem> locationData) {
        this.mContext = mContext;
        this.locationData = locationData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.location_item_view, viewGroup, false);
        return new myViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.locationPhoto.setImageResource(locationData.get(position).getLocationPhoto());
        holder.locationName.setText(locationData.get(position).getLocationName());
        holder.locationSummary.setText(locationData.get(position).getLocationSummary());
    }

    @Override
    public int getItemCount() {
        return locationData.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView locationPhoto;
        TextView locationName;
        TextView locationSummary;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            locationPhoto = itemView.findViewById(R.id.location_picture);
            locationName = itemView.findViewById(R.id.txt_location_name);
            locationSummary = itemView.findViewById(R.id.txt_location_summary);
        }
    }
}
