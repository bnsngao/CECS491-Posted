package com.example.posted;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PreferencesList extends AppCompatActivity
{
    ArrayList<String> selectedItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_pref);

        ListView ch1=(ListView)findViewById(R.id.preferences_list);

        ch1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        String[] preferences = {"Music","Food", "Drinks", "Movies", "Theater", "Sports", "Art"}; //add more preferences as we go
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.row_layout,R.id.txt_lan,preferences);
        // ch1.setAdapter(adapter);

        ch1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem=((TextView)view).getText().toString();
                if(selectedItems.contains(selectedItem))
                {
                    selectedItems.remove(selectedItem);//uncheck item
                }
                else
                    selectedItems.add(selectedItem);
            }
        });


    }
}
