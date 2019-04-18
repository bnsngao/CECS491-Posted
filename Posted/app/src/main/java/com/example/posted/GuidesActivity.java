package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class GuidesActivity extends AppCompatActivity {

    private RecyclerView guideList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guides);

        guideList = (RecyclerView) findViewById(R.id.guide_list);
        guideList.setHasFixedSize(true);
        LinearLayoutManager myManager = new LinearLayoutManager(this);
        myManager.setReverseLayout(true);
        myManager.setStackFromEnd(true);
        guideList.setLayoutManager(myManager);
    }
}
