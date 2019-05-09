package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;

public class Rate extends AppCompatActivity implements View.OnClickListener {
    private RatingBar ratingBar;
    private Button Rate;
    private Button Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .85), (int) (height * .2));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);
        ratingBar = findViewById(R.id.ratingBar);
        Rate = findViewById(R.id.rateButton);
        Cancel = findViewById(R.id.cancelButton);
        Rate.setOnClickListener(this);
        Cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==Rate){

        }
        else if (v==Cancel){
            finish();
        }
    }
}
