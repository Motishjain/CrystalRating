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
import android.support.design.widget.TextInputLayout;
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
import com.admin.receiver.CheckSubscriptionAlarmReceiver;
import com.admin.receiver.SetQuestionsAlarmReceiver;
import com.admin.receiver.DeviceBootReceiver;
import com.admin.services.RegistrationIntentService;
import com.admin.tasks.UpdateSubscriptionStatusTask;
import com.admin.util.ValidationUtil;
import com.admin.view.CustomProgressDialog;
import com.admin.webservice.RestEndpointInterface;
import com.admin.webservice.RetrofitSingleton;
import com.admin.webservice.request_objects.OutletRequest;
import com.admin.webservice.response_objects.SaveServiceReponse;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletDetailsActivity extends BaseActivity {

    TextInputLayout inputOutletNameLayout, inputaddressLine1Layout, inputaddressLine2Layout, inputPinCodeLayout, inputEmailLayout, inputPhoneNumberLayout;
    EditText outletName, addrLine1, addrLine2, pinCode, email, phoneNumber;
    Button nextButton, resetButton;
    TextView registerOutletHeader;
    Dao<Outlet, Integer> outletDao;
    Outlet currentOutlet;
    boolean editMode, aValidationError;
    String outletCode;
    ImageView activityBackButton;

    private AlarmManager alarmMgr;
    private PendingIntent setQuestionsAlarmIntent, checkSubscriptionAlarmIntent;
    private ProgressDialog progressDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_details);
        inputOutletNameLayout = (TextInputLayout) findViewById(R.id.inputOutletNameLayout);
        inputaddressLine1Layout = (TextInputLayout) findViewById(R.id.inputaddressLine1Layout);
        inputaddressLine2Layout = (TextInputLayout) findViewById(R.id.inputaddressLine2Layout);
        inputPinCodeLayout = (TextInputLayout) findViewById(R.id.inputPinCodeLayout);
        inputEmailLayout = (TextInputLayout) findViewById(R.id.inputEmailLayout);
        inputPhoneNumberLayout = (TextInputLayout) findViewById(R.id.inputPhoneNumberLayout);
        outletName = (EditText) findViewById(R.id.inputOutletNameText);
        addrLine1 = (EditText) findViewById(R.id.inputaddressLine1Text);
        addrLine2 = (EditText) findViewById(R.id.inputaddressLine2Text);
        pinCode = (EditText) findViewById(R.id.inputPinCodeText);
        email = (EditText) findViewById(R.id.inputEmailText);
        phoneNumber = (EditText) findViewById(R.id.inputPhoneNumberText);
        nextButton = (Button) findViewById(R.id.registerOutletNextButton);
        resetButton =(Button) findViewById(R.id.reset_button);
        registerOutletHeader = (TextView) findViewById(R.id.registerOutletHeader);
        activityBackButton = (ImageView) findViewById(R.id.activityBackButton);
        progressDialog = CustomProgressDialog.createCustomProgressDialog(this);

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editMode = extras.getBoolean("editMode", false);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            outletCode = sharedPreferences.getString("outletCode", null);
            populateFields(outletDao);
        }

        if (editMode) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        if (!editMode) {
            registerOutletHeader.setText("Register your Outlet!");
            nextButton.setText("Register");
            activityBackButton.setVisibility(View.GONE);
        } else {
            registerOutletHeader.setText("Edit Outlet Details");
            nextButton.setText("Update");
            activityBackButton.setVisibility(View.VISIBLE);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              ValidationUtil.isTextViewEmpty(outletName, inputOutletNameLayout, "Please enter the Outlet Name");
                                              ValidationUtil.isTextViewEmpty(addrLine1, inputaddressLine1Layout, "Please enter the Address Line1");
                                              ValidationUtil.isTextViewEmpty(addrLine2, inputaddressLine2Layout, "Please enter the Address Line2");
                                              ValidationUtil.isTextViewEmpty(pinCode, inputPinCodeLayout, "Please enter the Pin Code");
                                              ValidationUtil.isTextViewEmpty(email, inputEmailLayout, "Please enter the Email Id");
                                              ValidationUtil.isTextViewEmpty(phoneNumber, inputPhoneNumberLayout, "Please enter the Phone Number");

                                              if (!ValidationUtil.isTextViewEmpty(outletName, inputOutletNameLayout, "Please enter the Outlet Name") &&
                                                      !ValidationUtil.isTextViewEmpty(addrLine1, inputaddressLine1Layout, "Please enter the Address Line1") &&
                                                      !ValidationUtil.isTextViewEmpty(addrLine2, inputaddressLine2Layout, "Please enter the Address Line2") &&
                                                      !ValidationUtil.isTextViewEmpty(pinCode, inputPinCodeLayout, "Please enter the Pin Code") &&
                                                      !ValidationUtil.isTextViewEmpty(email, inputEmailLayout, "Please enter the Email Id") &&
                                                      !ValidationUtil.isTextViewEmpty(phoneNumber, inputPhoneNumberLayout, "Please enter the Phone Number"))
                                              {
                                                    if (editMode) {
                                                        progressDialog.setMessage("Updating Outlet Details...");
                                                    }
                                                    else {
                                                        progressDialog.setMessage("Registering Outlet...");
                                                    }

                                              progressDialog.show();
                                              final OutletRequest outletRequest = new OutletRequest();
                                              outletRequest.setOutletName(outletName.getText().toString());
                                              outletRequest.setAddrLine1(addrLine1.getText().toString());
                                              outletRequest.setOutletType(AppConstants.OUTLET_TYPE);
                                              outletRequest.setAddrLine2(addrLine2.getText().toString());
                                              outletRequest.setPinCode(pinCode.getText().toString());
                                              outletRequest.setEmail(email.getText().toString());
                                              outletRequest.setCellNumber(phoneNumber.getText().toString());
                                              outletRequest.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

                                              RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
                                              Call<SaveServiceReponse> registerOutletCall = restEndpointInterface.registerOutlet(outletRequest);
                                              registerOutletCall.enqueue(new Callback<SaveServiceReponse>() {
                                                  @Override
                                                  public void onResponse(Call<SaveServiceReponse> call, Response<SaveServiceReponse> response) {
                                                      SaveServiceReponse saveServiceReponse = response.body();
                                                      if (saveServiceReponse.isSuccess()) {
                                                          if (currentOutlet == null) {
                                                              currentOutlet = new Outlet();
                                                          }
                                                          currentOutlet.setOutletCode(saveServiceReponse.getData().toString());
                                                          currentOutlet.setOutletName(outletRequest.getOutletName());
                                                          currentOutlet.setAddrLine1(outletRequest.getAddrLine1());
                                                          currentOutlet.setAddrLine2(outletRequest.getAddrLine2());
                                                          currentOutlet.setPinCode(outletRequest.getPinCode());
                                                          currentOutlet.setEmail(outletRequest.getEmail());
                                                          currentOutlet.setCellNumber(outletRequest.getCellNumber());

                                                          //Check if this is create mode (Register Outlet)
                                                          if (!editMode) {
                                                              try {

                                                                  //Register GCM token with app server
                                                                  Intent gcmRegistrationIntent = new Intent(OutletDetailsActivity.this, RegistrationIntentService.class);
                                                                  startService(gcmRegistrationIntent);
                                                                  // Set the alarm to start at approximately 12:00 a.m. to run scheduled job

                                                                  alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                                                                  Intent setQuestionsIntent = new Intent(OutletDetailsActivity.this, SetQuestionsAlarmReceiver.class);
                                                                  setQuestionsAlarmIntent = PendingIntent.getBroadcast(OutletDetailsActivity.this, 0, setQuestionsIntent, 0);

                                                                  Intent checkSubscriptionIntent = new Intent(OutletDetailsActivity.this, CheckSubscriptionAlarmReceiver.class);
                                                                  checkSubscriptionAlarmIntent = PendingIntent.getBroadcast(OutletDetailsActivity.this, 0, checkSubscriptionIntent, 0);

                                                                  Calendar calendar = Calendar.getInstance();
                                                                  calendar.setTimeInMillis(System.currentTimeMillis());
                                                                  calendar.set(Calendar.HOUR_OF_DAY, 0);
                                                                  alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                                                          AlarmManager.INTERVAL_DAY, setQuestionsAlarmIntent);

                                                                  calendar.set(Calendar.HOUR_OF_DAY, 14);

                                                                  alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                                                                          AlarmManager.INTERVAL_DAY, checkSubscriptionAlarmIntent);

                                                                  ComponentName receiver = new ComponentName(OutletDetailsActivity.this, DeviceBootReceiver.class);
                                                                  PackageManager pm = getApplicationContext().getPackageManager();
                                                                  pm.setComponentEnabledSetting(receiver,
                                                                          PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                                                          PackageManager.DONT_KILL_APP);
                                                                  outletDao.create(currentOutlet);
                                                                  UpdateSubscriptionStatusTask updateSubscriptionStatusTask = new UpdateSubscriptionStatusTask(OutletDetailsActivity.this);
                                                                  updateSubscriptionStatusTask.execute();
                                                                  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                                  SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                  editor.putString("outletCode", currentOutlet.getOutletCode());
                                                                  editor.commit();
                                                                  progressDialog.dismiss();
                                                                  Intent configureRewards = new Intent(OutletDetailsActivity.this, RewardConfigurationActivity.class);
                                                                  configureRewards.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                  startActivity(configureRewards);
                                                              } catch (SQLException e) {
                                                                  e.printStackTrace();
                                                              }
                                                          } else {
                                                              try {
                                                                  UpdateBuilder<Outlet, Integer> outletUpdateBuilder = outletDao.updateBuilder();
                                                                  outletUpdateBuilder.updateColumnValue("outletName", currentOutlet.getOutletName());
                                                                  outletUpdateBuilder.updateColumnValue("addrLine1", currentOutlet.getAddrLine1());
                                                                  outletUpdateBuilder.updateColumnValue("addrLine2", currentOutlet.getAddrLine2());
                                                                  outletUpdateBuilder.updateColumnValue("pinCode", currentOutlet.getPinCode());
                                                                  outletUpdateBuilder.updateColumnValue("email", currentOutlet.getEmail());
                                                                  outletUpdateBuilder.updateColumnValue("cellNumber", currentOutlet.getCellNumber());
                                                                  outletUpdateBuilder.where().eq("outletCode", currentOutlet.getOutletCode());
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
                                                  public void onFailure(Call<SaveServiceReponse> call, Throwable t) {
                                                      Log.e("Outlet details", "Unable to save outlet");
                                                  }
                                              });
                                          }
                                          }

        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidationUtil.resetForm(outletName, inputOutletNameLayout);
                ValidationUtil.resetForm(addrLine1, inputaddressLine1Layout);
                ValidationUtil.resetForm(addrLine2, inputaddressLine2Layout);
                ValidationUtil.resetForm(pinCode, inputPinCodeLayout);
                ValidationUtil.resetForm(email, inputEmailLayout);
                ValidationUtil.resetForm(phoneNumber, inputPhoneNumberLayout);
            }
        });
    }

            void populateFields(Dao<Outlet, Integer> outletDao) {

                QueryBuilder<Outlet, Integer> outletQueryBuilder = outletDao.queryBuilder();
                try {
                    currentOutlet = outletQueryBuilder.queryForFirst();
                    outletName.setText(currentOutlet.getOutletName());
                    addrLine1.setText(currentOutlet.getAddrLine1());
                    addrLine2.setText(currentOutlet.getAddrLine2());
                    pinCode.setText(currentOutlet.getPinCode());
                    email.setText(currentOutlet.getEmail());
                    phoneNumber.setText(currentOutlet.getCellNumber());
                } catch (SQLException e) {
                    Log.e("OutletDetailsActivity", "Outlet details fetch error");
                }
            }

            public void closeActivity(View v) {
                if (editMode) {
                    this.finish();
                }
            }
        }
