package com.admin.freddyspeaks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.adapter.UserPhoneNumberInputAdapter;
import com.admin.database.DBHelper;
import com.admin.database.User;
import com.admin.util.ImageUtility;
import com.admin.util.ValidationUtil;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends BaseActivity {

    ImageView backgroundRatingImage;
    Button getStartedButton;
    TextInputLayout inputUserPhoneNumberLayout;
    AutoCompleteTextView autoCompleteInputUserPhoneNumberText;
    Dao<User, Integer> userDao;
    QueryBuilder<User, Integer> queryBuilder;
    List<User> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            userDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("User");
            queryBuilder = userDao.queryBuilder();
            userList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }

        backgroundRatingImage = (ImageView) findViewById(R.id.backgroundRatingImage);
        backgroundRatingImage.setImageBitmap(ImageUtility.getImageBitmap(R.drawable.shopping_bg));
        getStartedButton = (Button) findViewById(R.id.getStartedButton);
        inputUserPhoneNumberLayout = (TextInputLayout) findViewById(R.id.inputUserPhoneNumberLayout);
        autoCompleteInputUserPhoneNumberText =(AutoCompleteTextView)findViewById(R.id.autoCompleteInputUserPhoneNumberText);

        autoCompleteInputUserPhoneNumberText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if ((event != null)){
                    if(ValidationUtil.isValidCellNumber(autoCompleteInputUserPhoneNumberText,inputUserPhoneNumberLayout,"Please enter valid Mobile Number ")){
                        saveAndNext();
                    }
                }
                return false;
            }
        });

        final UserPhoneNumberInputAdapter adapter = new UserPhoneNumberInputAdapter(this,
                R.layout.userinfo_autosuggest, userList);

        autoCompleteInputUserPhoneNumberText.setAdapter(adapter);
        autoCompleteInputUserPhoneNumberText.addTextChangedListener(mWatcher);
        getStartedButton.setEnabled(false);
        ImageUtility.setButtonLook(this,false,getStartedButton);
        getStartedButton.setTextColor(this.getResources().getColor(R.color.disabled_button_color));

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndNext();
            }
        });
        getStartedButton.addTextChangedListener(mWatcher);
    }

    void saveAndNext() {
        if(ValidationUtil.isValidCellNumber(autoCompleteInputUserPhoneNumberText,inputUserPhoneNumberLayout,"Please enter valid Mobile Number")) {
            try {
                queryBuilder.reset();
                queryBuilder.where().eq("phoneNumber", autoCompleteInputUserPhoneNumberText.getText().toString().trim());
                userList = queryBuilder.query();
                if (userList == null || userList.size() == 0) {
                    User newUser = new User();
                    newUser.setPhoneNumber(autoCompleteInputUserPhoneNumberText.getText().toString());
                    userDao.create(newUser);
                }
                Intent getRating = new Intent(HomePageActivity.this, GetRatingActivity.class);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String outletCode = sharedPreferences.getString("outletCode", null);
                FeedbackRequest feedback = new FeedbackRequest();
                feedback.setOutletCode(outletCode);
                feedback.setUserPhoneNumber(autoCompleteInputUserPhoneNumberText.getText().toString());
                getRating.putExtra("feedback", feedback);
                startActivity(getRating);
            } catch (SQLException e) {
                Log.e("HomePageActivity", "Failed to save user", e);
            }
        }
    }

    TextWatcher mWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            MobileNumberCompletion();
        }

        @Override
        public void afterTextChanged(Editable s) {
            MobileNumberCompletion();
        }
    };

    private void MobileNumberCompletion()
    {
        if(autoCompleteInputUserPhoneNumberText.getText().length()==10)
        {
            ImageUtility.setButtonLook(this,true,getStartedButton);
            getStartedButton.setEnabled(true);
        }
        else
        {
            ImageUtility.setButtonLook(this,false,getStartedButton);
            getStartedButton.setEnabled(false);
        }

    }



}