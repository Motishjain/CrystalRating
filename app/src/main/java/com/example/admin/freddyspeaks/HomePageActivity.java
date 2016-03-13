package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.admin.value_objects.Feedback;

public class HomePageActivity extends AppCompatActivity {

    Button addRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addRating = (Button) findViewById(R.id.add_rating);
        Bundle extras = getIntent().getExtras();
        final String outletCode = extras.getString("outletCode");

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent billDetails = new Intent(HomePageActivity.this, BillDetailsActivity.class);
                Feedback feedback = new Feedback();
                feedback.setOutletCode(outletCode);
                billDetails.putExtra("feedback",feedback);
                startActivity(billDetails);
            }
        });
    }
}