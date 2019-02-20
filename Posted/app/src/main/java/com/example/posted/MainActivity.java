package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<ProfileItem> mList = new ArrayList<>();
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());
        mList.add(new ProfileItem());


        //set up recycler view with adapter
        RecyclerView recyclerView = findViewById(R.id.rv_profile_list);
        Adapter adapter = new Adapter(this, mList);
    }
}
