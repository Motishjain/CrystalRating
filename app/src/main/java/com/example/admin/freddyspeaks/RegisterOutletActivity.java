package com.example.admin.freddyspeaks;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Outlet;
import com.example.admin.database.Question;
import com.example.admin.database.Reward;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.response_objects.QuestionResponse;
import com.example.admin.webservice.response_objects.RewardResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterOutletActivity extends AppCompatActivity {

    EditText outletName, alias, addrLine1, addrLine2, pinCode, email, phoneNumber;
    Button nextButton;
    Dao<Outlet, Integer> outletDao;
    Dao<Question, Integer> questionDao;
    TextToSpeech textToSpeechConverter;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_outlet);
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

        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Outlet");
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");

        } catch (Exception e) {
            e.printStackTrace();
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Outlet newOutlet = new Outlet();
                newOutlet.setOutletName(outletName.getText().toString());
                newOutlet.setAliasName(alias.getText().toString());
                newOutlet.setAddrLine1(addrLine1.getText().toString());
                newOutlet.setAddrLine2(addrLine2.getText().toString());
                newOutlet.setPinCode(pinCode.getText().toString());
                newOutlet.setEmail(email.getText().toString());
                newOutlet.setCellNumber(phoneNumber.getText().toString());
                try {
                    outletDao.create(newOutlet);
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO logging
                }
                //TODO web service call to fetch - move to save of reward configuration
                RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
                Call<List<QuestionResponse>> fetchQuestionsCall = restEndpointInterface.fetchQuestions(AppConstants.OUTLET_TYPE);
                fetchQuestionsCall.enqueue(new Callback<List<QuestionResponse>>() {
                    @Override
                    public void onResponse(Call<List<QuestionResponse>> call, Response<List<QuestionResponse>> response) {
                        List<QuestionResponse> questionsList = response.body();
                        try {
                            for (QuestionResponse questionResponse : questionsList) {
                                Question dbQuestion = new Question();
                                dbQuestion.setQuestionId(questionResponse.getQuestionId());
                                dbQuestion.setName(questionResponse.getQuestionName());
                                dbQuestion.setRatingValues(android.text.TextUtils.join(",", questionResponse.getOptionValues()));
                                dbQuestion.setSelected("Y");
                                questionDao.create(dbQuestion);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<QuestionResponse>> call, Throwable t) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register_company, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
