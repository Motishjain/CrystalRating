package com.example.admin.freddyspeaks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutletDetailsActivity extends AppCompatActivity {

    EditText outletName, alias, addrLine1, addrLine2, pinCode, email, phoneNumber;
    Button nextButton;
    TextView registerOutletHeader;
    Dao<Outlet, Integer> outletDao;
    TextToSpeech textToSpeechConverter;
    boolean editMode;
    Gson gson;


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

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            editMode = extras.getBoolean("editMode",false);
        }

        if(!editMode) {
            registerOutletHeader.setText("Register your Outlet!");
            nextButton.setText("Register");
        }
        else {
            registerOutletHeader.setText("Edit Outlet Details");
            nextButton.setText("Update");
        }

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Outlet");
        } catch (Exception e) {
            e.printStackTrace();
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO web service call to fetch - move to save of reward configuration
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
                            Outlet newOutlet = new Outlet();
                            newOutlet.setOutletCode(postServiceResponse.getData().toString());
                            newOutlet.setOutletName(outletRequest.getOutletName());
                            newOutlet.setAliasName(outletRequest.getAliasName());
                            newOutlet.setAddrLine1(outletRequest.getAddrLine1());
                            newOutlet.setAddrLine2(outletRequest.getAddrLine2());
                            newOutlet.setPinCode(outletRequest.getPinCode());
                            newOutlet.setEmail(outletRequest.getEmail());
                            newOutlet.setCellNumber(outletRequest.getCellNumber());
                            try {
                                outletDao.create(newOutlet);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("outletCode", newOutlet.getOutletCode());
                            editor.commit();

                            Intent configureRewards = new Intent(OutletDetailsActivity.this, RewardConfigurationActivity.class);
                            startActivity(configureRewards);
                        }
                    }

                    @Override
                    public void onFailure(Call<PostServiceResponse> call, Throwable t) {

                    }
                });


            }

        });

/*        textToSpeechConverter=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
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

}