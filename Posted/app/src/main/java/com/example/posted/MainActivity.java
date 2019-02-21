package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<LocationItem> mList = new ArrayList<>();
        mList.add(new LocationItem());
        mList.add(new LocationItem());
        mList.add(new LocationItem());
        mList.add(new LocationItem());


        //set up recycler view with profileAdapter
        RecyclerView recyclerView = findViewById(R.id.rv_location_list);
        LocationAdapter locationAdapter = new LocationAdapter(this, mList);
        recyclerView.setAdapter(locationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*
        //Window w = getWindow();
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        List<ProfileItem> mList = new ArrayList<>();
        mList.add(new ProfileItem(R.drawable.senior_woman, 2, "Lady"));
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());


        //set up recycler view with profileAdapter
        RecyclerView recyclerView = findViewById(R.id.rv_profile_list);
        ProfileAdapter profileAdapter = new ProfileAdapter(this, mList);
        recyclerView.setAdapter(profileAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        */
    }
}
