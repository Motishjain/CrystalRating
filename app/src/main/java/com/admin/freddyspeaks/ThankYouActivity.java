package com.admin.freddyspeaks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.util.ImageUtility;
import com.admin.view.CustomFontTextView;
import com.admin.webservice.WebServiceUtility;
import com.admin.webservice.request_objects.FeedbackRequest;

import org.w3c.dom.Text;

public class ThankYouActivity extends AppCompatActivity {

    ImageView backgroundRatingImage;
    CustomFontTextView thankYouMessage1,thankYouMessage2;
    Button exitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);
        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(R.drawable.shopping_bg));

        thankYouMessage1=(CustomFontTextView) findViewById(R.id.rewardDisplayMessage);
        thankYouMessage2=(CustomFontTextView) findViewById(R.id.rewardDisplayMessage1);
        exitButton=(Button) findViewById(R.id.thankYouExitButton);
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

