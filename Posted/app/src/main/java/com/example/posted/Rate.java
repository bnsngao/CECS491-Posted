package com.example.posted;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Rate extends AppCompatActivity implements View.OnClickListener {
    private RatingBar ratingBar;
    private Button Rate;
    private Button Cancel;
    private String guideID;
    private String currentUID;

    private FirebaseAuth mAuth;

    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        currentUID = mAuth.getCurrentUser().getUid();

        userReference = FirebaseDatabase.getInstance().getReference();

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
        guideID = getIntent().getStringExtra("guideID");
        System.out.println(guideID);

    }

    @Override
    public void onClick(View v) {
        if(v==Rate){
            float rating = ratingBar.getRating();
            userReference.child("users").child(guideID).child("ratings").child(currentUID).setValue(rating);
        }
        else if (v==Cancel){
            finish();
        }
    }
}
