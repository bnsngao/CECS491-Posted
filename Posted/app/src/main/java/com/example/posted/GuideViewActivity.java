package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GuideViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<ProfileItem> mList = new ArrayList<>();
        mList.add(new ProfileItem(R.drawable.profile_test, 2, "Lady"));
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());


        //set up recycler view with profileAdapter
        RecyclerView recyclerView = findViewById(R.id.rv_profile_list);
        ProfileAdapter profileAdapter = new ProfileAdapter(this, mList);
        recyclerView.setAdapter(profileAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
