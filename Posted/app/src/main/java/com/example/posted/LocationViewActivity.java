package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LocationViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover_locations);

        List<LocationItem> mList = new ArrayList<>();
        String temp;
        for(int i = 0; i < 10; i++) {
            temp = "Location #" + (i + 1);
            mList.add(new LocationItem(R.drawable.location_test, temp, temp + " test summary"));
        }

        //set up recycler view with profileAdapter
        RecyclerView recyclerView = findViewById(R.id.rv_location_list);
        LocationAdapter locationAdapter = new LocationAdapter(this, mList);
        recyclerView.setAdapter(locationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
