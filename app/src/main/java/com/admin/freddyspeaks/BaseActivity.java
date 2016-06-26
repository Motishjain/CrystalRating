package com.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.admin.dialogs.EnterPinDialogFragment;

/**
 * Created by Admin on 3/27/2016.
 */
public class BaseActivity  extends AppCompatActivity {

    EnterPinDialogFragment enterPinDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.actionEditProfile:

                enterPinDialogFragment = EnterPinDialogFragment.newInstance("", new EnterPinDialogFragment.EnterPinDialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        enterPinDialogFragment.dismiss();
                        Intent outletDetails = new Intent(BaseActivity.this,OutletDetailsActivity.class);
                        outletDetails.putExtra("editMode",true);
                        startActivity(outletDetails);
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        enterPinDialogFragment.dismiss();
                    }
                });
                enterPinDialogFragment.show(getSupportFragmentManager(), "");
                return true;

            case R.id.actionRewards:
                enterPinDialogFragment = EnterPinDialogFragment.newInstance("", new EnterPinDialogFragment.EnterPinDialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        enterPinDialogFragment.dismiss();
                        Intent configureRewards = new Intent(BaseActivity.this,RewardConfigurationActivity.class);
                        configureRewards.putExtra("editMode",true);
                        startActivity(configureRewards);
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        enterPinDialogFragment.dismiss();
                    }
                });
                enterPinDialogFragment.show(getSupportFragmentManager(), "");
                return true;

            case R.id.actionSubscriptionInfo:
                enterPinDialogFragment = EnterPinDialogFragment.newInstance("", new EnterPinDialogFragment.EnterPinDialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        enterPinDialogFragment.dismiss();
                        Intent subscriptionInfo = new Intent(BaseActivity.this,SubscriptionInfoActivity.class);
                        startActivity(subscriptionInfo);
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        enterPinDialogFragment.dismiss();
                    }
                });
                enterPinDialogFragment.show(getSupportFragmentManager(), "");
                return true;

            case R.id.actionFeedbackSummary:
                enterPinDialogFragment = EnterPinDialogFragment.newInstance("", new EnterPinDialogFragment.EnterPinDialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        enterPinDialogFragment.dismiss();
                        Intent ratingSummary = new Intent(BaseActivity.this,FeedbackAnalysisActivity.class);
                        startActivity(ratingSummary);
                    }

                    @Override
                    public void onDialogNegativeClick() {
                        enterPinDialogFragment.dismiss();
                    }
                });
                enterPinDialogFragment.show(getSupportFragmentManager(), "");
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
}