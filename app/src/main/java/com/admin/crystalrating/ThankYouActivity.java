package com.admin.crystalrating;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.admin.dialogs.CustomDialogFragment;
import com.admin.util.ImageUtility;
import com.admin.view.CustomFontTextView;
import com.admin.webservice.WebServiceUtility;
import com.admin.webservice.request_objects.FeedbackRequest;

public class ThankYouActivity extends AppCompatActivity {

    ImageView backgroundRatingImage;
    CustomFontTextView thankYouMessage1,thankYouMessage2;
    Button exitButton;
    CustomDialogFragment dialogConfirmExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);
        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(this,R.drawable.shopping_bg));

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

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Intent homePage = new Intent(ThankYouActivity.this, HomePageActivity.class);
                homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homePage);
            }
        }, 3*60*1000);
    }

    @Override
    public void onBackPressed() {
        dialogConfirmExit = CustomDialogFragment.newInstance(R.layout.dialog_confirm_exit, new CustomDialogFragment.CustomDialogListener() {
            @Override
            public void onDialogPositiveClick() {
                dialogConfirmExit.dismiss();
                Intent homePage = new Intent(ThankYouActivity.this, HomePageActivity.class);
                homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homePage);
            }

            @Override
            public void onDialogNegativeClick() {
                dialogConfirmExit.dismiss();
            }
        });
        dialogConfirmExit.show(getSupportFragmentManager(), "");
    }
}

