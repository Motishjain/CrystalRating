package com.admin.freddyspeaks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.admin.webservice.WebServiceUtility;
import com.admin.webservice.request_objects.FeedbackRequest;

public class ThankYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        Bundle extras = getIntent().getExtras();
        FeedbackRequest feedback = (FeedbackRequest)extras.get("feedback");
        WebServiceUtility.submitFeedback(this,feedback);
    }

    public void exit(View v) {
        Intent homePage = new Intent(ThankYouActivity.this, HomePageActivity.class);
        homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homePage);
    }
}

