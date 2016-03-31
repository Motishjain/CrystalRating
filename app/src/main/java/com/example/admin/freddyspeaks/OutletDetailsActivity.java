package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Outlet;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.request_objects.OutletRequest;
import com.example.admin.webservice.response_objects.PostServiceResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletDetailsActivity extends AppCompatActivity {

    EditText outletName, alias, addrLine1, addrLine2, pinCode, email, phoneNumber;
    Button nextButton;
    TextView registerOutletHeader;
    Dao<Outlet, Integer> outletDao;
    Outlet currentOutlet;
    boolean editMode;
    String outletCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        outletName = (EditText) findViewById(R.id.inputOutletNameText);
        alias = (EditText) findViewById(R.id.inputAliasNameText);
        addrLine1 = (EditText) findViewById(R.id.inputaddressLine1Text);
        addrLine2 = (EditText) findViewById(R.id.inputaddressLine2Text);
        pinCode = (EditText) findViewById(R.id.inputPinCodeText);
        email = (EditText) findViewById(R.id.inputEmailText);
        phoneNumber = (EditText) findViewById(R.id.inputPhoneNumberText);
        nextButton = (Button) findViewById(R.id.registerOutletNextButton);
        registerOutletHeader = (TextView) findViewById(R.id.registerOutletHeader);

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            editMode = extras.getBoolean("editMode",false);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            outletCode = sharedPreferences.getString("outletCode", null) ;
            populateFields(outletDao);
        }

        if(!editMode) {
            registerOutletHeader.setText("Register your Outlet!");
            nextButton.setText("Register");
        }
        else {
            registerOutletHeader.setText("Edit Outlet Details");
            nextButton.setText("Update");
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                        if (postServiceResponse.isSuccess()) {
                            if(!editMode) {
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
                            try {
                                outletDao.create(currentOutlet);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("outletCode", currentOutlet.getOutletCode());
                            editor.commit();

                            Intent configureRewards = new Intent(OutletDetailsActivity.this, RewardConfigurationActivity.class);
                            startActivity(configureRewards);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostServiceResponse> call, Throwable t) {
                        //TODO handle failure
                    }
                });


            }

        });

        /*textToSpeechConverter=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    new Thread(new Runnable() {
                        public void run() {
                            textToSpeechConverter.setPitch(1.1f); // saw from internet
                            textToSpeechConverter.setSpeechRate(0.4f); // f denotes float, it actually type casts 0.5 to float
                            textToSpeechConverter.setLanguage(Locale.UK);
                            textToSpeechConverter.speak(AppConstants.REGISTER_WELCOME_MSG, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }).start();
                }
            }
        });*/
    }

    void populateFields(Dao<Outlet,Integer> outletDao) {

        QueryBuilder<Outlet,Integer> outletQueryBuilder = outletDao.queryBuilder();
        Outlet currentOutlet;
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
}
