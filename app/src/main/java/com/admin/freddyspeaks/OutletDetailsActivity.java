package com.admin.freddyspeaks;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.constants.AppConstants;
import com.admin.database.DBHelper;
import com.admin.database.Outlet;
import com.admin.receiver.AlarmReceiver;
import com.admin.receiver.DeviceBootReceiver;
import com.admin.util.DialogBuilderUtil;
import com.admin.view.CustomProgressDialog;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.request_objects.OutletRequest;
import com.admin.webservice.response_objects.PostServiceResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletDetailsActivity extends BaseActivity {

    EditText outletName, alias, addrLine1, addrLine2, pinCode, email, phoneNumber;
    Button nextButton;
    TextView registerOutletHeader;
    Dao<Outlet, Integer> outletDao;
    Outlet currentOutlet;
    boolean editMode;
    String outletCode;
    ImageView activityBackButton;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_details);

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            editMode = extras.getBoolean("editMode",false);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            outletCode = sharedPreferences.getString("outletCode", null) ;
            populateFields(outletDao);
        }

        if(editMode) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        outletName = (EditText) findViewById(R.id.inputOutletNameText);
        alias = (EditText) findViewById(R.id.inputAliasNameText);
        addrLine1 = (EditText) findViewById(R.id.inputaddressLine1Text);
        addrLine2 = (EditText) findViewById(R.id.inputaddressLine2Text);
        pinCode = (EditText) findViewById(R.id.inputPinCodeText);
        email = (EditText) findViewById(R.id.inputEmailText);
        phoneNumber = (EditText) findViewById(R.id.inputPhoneNumberText);
        nextButton = (Button) findViewById(R.id.registerOutletNextButton);
        registerOutletHeader = (TextView) findViewById(R.id.registerOutletHeader);
        activityBackButton = (ImageView) findViewById(R.id.activityBackButton);
        progressDialog = CustomProgressDialog.createCustomProgressDialog(this);

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!editMode) {
            registerOutletHeader.setText("Register your Outlet!");
            nextButton.setText("Register");
            activityBackButton.setVisibility(View.GONE);
        }
        else {
            registerOutletHeader.setText("Edit Outlet Details");
            nextButton.setText("Update");
            activityBackButton.setVisibility(View.VISIBLE);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode)
                {
                    progressDialog.setMessage("Updating Outlet Details...");
                }
                else {
                    progressDialog.setMessage("Registering Outlet...");
                }
                progressDialog.show();
                long T1 = android.os.SystemClock.uptimeMillis();
                Log.d("GetRatingT1",T1+"");
                final OutletRequest outletRequest = new OutletRequest();
                outletRequest.setOutletName(outletName.getText().toString());
                outletRequest.setAliasName(alias.getText().toString());
                outletRequest.setAddrLine1(addrLine1.getText().toString());
                outletRequest.setOutletType(AppConstants.OUTLET_TYPE);
                outletRequest.setAddrLine2(addrLine2.getText().toString());
                outletRequest.setPinCode(pinCode.getText().toString());
                outletRequest.setEmail(email.getText().toString());
                outletRequest.setCellNumber(phoneNumber.getText().toString());

                RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
                Call<PostServiceResponse> registerOutletCall = restEndpointInterface.registerOutlet(outletRequest);
                registerOutletCall.enqueue(new Callback<PostServiceResponse>() {
                    @Override
                    public void onResponse(Call<PostServiceResponse> call, Response<PostServiceResponse> response) {
                        PostServiceResponse postServiceResponse = response.body();

                        long T2 = android.os.SystemClock.uptimeMillis();
                        Log.d("GetRatingT2",T2+"");

                        if (postServiceResponse.isSuccess()) {
                            if(currentOutlet == null) {
                                currentOutlet = new Outlet();
                            }
                            currentOutlet.setOutletCode(postServiceResponse.getData().toString());
                            currentOutlet.setOutletName(outletRequest.getOutletName());
                            currentOutlet.setAliasName(outletRequest.getAliasName());
                            currentOutlet.setAddrLine1(outletRequest.getAddrLine1());
                            currentOutlet.setAddrLine2(outletRequest.getAddrLine2());
                            currentOutlet.setPinCode(outletRequest.getPinCode());
                            currentOutlet.setEmail(outletRequest.getEmail());
                            currentOutlet.setCellNumber(outletRequest.getCellNumber());

                            //Check if this is create mode (Register Outlet)
                            if(!editMode) {
                                try {
                                    // Set the alarm to start at approximately 12:00 a.m. to run scheduled job

                                    alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                    Intent intent = new Intent(OutletDetailsActivity.this, AlarmReceiver.class);
                                    alarmIntent = PendingIntent.getBroadcast(OutletDetailsActivity.this, 0, intent, 0);

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(System.currentTimeMillis());
                                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                                    alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                            AlarmManager.INTERVAL_DAY, alarmIntent);
                                    ComponentName receiver = new ComponentName(OutletDetailsActivity.this, DeviceBootReceiver.class);
                                    PackageManager pm = getApplicationContext().getPackageManager();
                                    pm.setComponentEnabledSetting(receiver,
                                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                            PackageManager.DONT_KILL_APP);
                                    outletDao.create(currentOutlet);
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("outletCode", currentOutlet.getOutletCode());
                                    editor.commit();
                                    long T3 = android.os.SystemClock.uptimeMillis();
                                    Log.d("GetRatingT3",T3+"");
                                    progressDialog.dismiss();
                                    Intent configureRewards = new Intent(OutletDetailsActivity.this, RewardConfigurationActivity.class);
                                    startActivity(configureRewards);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                try {
                                    UpdateBuilder<Outlet, Integer> outletUpdateBuilder = outletDao.updateBuilder();
                                    outletUpdateBuilder.updateColumnValue("outletName",currentOutlet.getOutletName());
                                    outletUpdateBuilder.updateColumnValue("aliasName",currentOutlet.getAliasName());
                                    outletUpdateBuilder.updateColumnValue("addrLine1",currentOutlet.getAddrLine1());
                                    outletUpdateBuilder.updateColumnValue("addrLine2",currentOutlet.getAddrLine2());
                                    outletUpdateBuilder.updateColumnValue("pinCode",currentOutlet.getPinCode());
                                    outletUpdateBuilder.updateColumnValue("email",currentOutlet.getEmail());
                                    outletUpdateBuilder.updateColumnValue("cellNumber",currentOutlet.getCellNumber());
                                    outletUpdateBuilder.where().eq("outletCode",currentOutlet.getOutletCode());
                                    outletUpdateBuilder.update();
                                    progressDialog.dismiss();
                                    OutletDetailsActivity.this.finish();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PostServiceResponse> call, Throwable t) {
                        Log.e("Outlet details","Unable to save outlet");
                    }
                });


            }

        });
    }

    void populateFields(Dao<Outlet,Integer> outletDao) {

        QueryBuilder<Outlet,Integer> outletQueryBuilder = outletDao.queryBuilder();
        try {
            currentOutlet = outletQueryBuilder.queryForFirst();
            outletName.setText(currentOutlet.getOutletName());
            alias.setText(currentOutlet.getAliasName());
            addrLine1.setText(currentOutlet.getAddrLine1());
            addrLine2.setText(currentOutlet.getAddrLine2());
            pinCode.setText(currentOutlet.getPinCode());
            email.setText(currentOutlet.getEmail());
            phoneNumber.setText(currentOutlet.getCellNumber());
        }
        catch (SQLException e) {
            Log.e("OutletDetailsActivity","Outlet details fetch error");
        }
    }

    public void closeActivity(View v) {
        this.finish();
    }
}
