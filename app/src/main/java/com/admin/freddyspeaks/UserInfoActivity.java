package com.admin.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.admin.adapter.UserPhoneNumberInputAdapter;
import com.admin.database.DBHelper;
import com.admin.database.User;
import com.admin.webservice.request_objects.FeedbackRequest;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends BaseActivity {

    EditText userName;
    TextInputLayout userNameLayout, userPhoneLayout;
    Button continueButton, resetButton;
    AutoCompleteTextView autoCompleteInputUserPhoneNumberText;
    Dao<User, Integer> userDao;
    QueryBuilder<User, Integer> queryBuilder;
    UpdateBuilder<User, Integer> updateBuilder;
    List<User> userList = new ArrayList<>();
    FeedbackRequest feedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Bundle extras = getIntent().getExtras();
        feedback = (FeedbackRequest)extras.get("feedback");

        try {
            userDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("User");
            queryBuilder = userDao.queryBuilder();
            updateBuilder = userDao.updateBuilder();
            userList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }

        autoCompleteInputUserPhoneNumberText =(AutoCompleteTextView)findViewById(R.id.autoCompleteInputUserPhoneNumberText);

        final UserPhoneNumberInputAdapter adapter = new UserPhoneNumberInputAdapter(this,
                R.layout.userinfo_autosuggest, userList);

        autoCompleteInputUserPhoneNumberText.setAdapter(adapter);

        autoCompleteInputUserPhoneNumberText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                User selectedUser = adapter.getItem(position);
                userName.setText(selectedUser.getName());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (EditText) findViewById(R.id.inputUserNameText);

        userNameLayout = (TextInputLayout) findViewById(R.id.inputUserNameLayout);
        userPhoneLayout = (TextInputLayout) findViewById(R.id.inputUserPhoneNumberLayout);

        continueButton = (Button) findViewById(R.id.userInfoContinueButton);
        resetButton = (Button) findViewById(R.id.userInfoResetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteInputUserPhoneNumberText.setText("");
                userName.setText("");
                userNameLayout.setError(null);
                userPhoneLayout.setError(null);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allFieldsEntered = true;
                if (autoCompleteInputUserPhoneNumberText.getText().toString().equals("")) {
                    userNameLayout.setError("Please enter name");
                    allFieldsEntered = false;
                }
                if (userName.getText().toString().equals("")) {
                    userPhoneLayout.setError("Please enter phone number");
                    userName.findFocus();
                    allFieldsEntered = false;
                }
                if(allFieldsEntered) {
                    try {
                        queryBuilder.reset();
                        queryBuilder.where().eq("phoneNumber",autoCompleteInputUserPhoneNumberText.getText().toString().trim());
                        userList = queryBuilder.query();
                        if(userList!=null && userList.size()>0) {
                            updateBuilder.reset();
                            updateBuilder.where().eq("phoneNumber",autoCompleteInputUserPhoneNumberText.getText().toString().trim());
                            updateBuilder.updateColumnValue("name",userName.getText().toString().trim());
                            updateBuilder.update();
                        }
                        else {
                            User newUser = new User();
                            newUser.setName(userName.getText().toString().trim());
                            newUser.setPhoneNumber(autoCompleteInputUserPhoneNumberText.getText().toString());
                            userDao.create(newUser);
                        }
                        Intent getBillInfo = new Intent(UserInfoActivity.this, BillDetailsActivity.class);
                        feedback.setUserPhoneNumber(autoCompleteInputUserPhoneNumberText.getText().toString());
                        feedback.setUserName(userName.getText().toString());
                        getBillInfo.putExtra("feedback",feedback);
                        startActivity(getBillInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}