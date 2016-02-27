package com.example.mjai37.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.mjai37.adapter.UserNameInputAdapter;
import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.User;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.util.ArrayList;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    EditText userPhoneNumber;
    TextInputLayout userNameLayout, userPhoneLayout;
    Button continueButton, resetButton;
    AutoCompleteTextView autoCompleteInputUserNameText;
    Dao<User, Integer> userDao;
    QueryBuilder<User, Integer> queryBuilder;
    UpdateBuilder<User, Integer> updateBuilder;
    List<User> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        try {
            userDao = OpenHelperManager.getHelper(this, DBHelper.class).getUserDao();
            queryBuilder = userDao.queryBuilder();
            updateBuilder = userDao.updateBuilder();
            userList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }

        autoCompleteInputUserNameText =(AutoCompleteTextView)findViewById(R.id.autoCompleteInputUserNameText);

        final UserNameInputAdapter adapter = new UserNameInputAdapter(this,
                R.layout.userinfo_autosuggest, userList);

        autoCompleteInputUserNameText.setAdapter(adapter);

        autoCompleteInputUserNameText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                User selectedUser = adapter.getItem(position);
                userPhoneNumber.setText(selectedUser.getPhoneNumber());
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userPhoneNumber = (EditText) findViewById(R.id.inputUserPhoneNumberText);

        userNameLayout = (TextInputLayout) findViewById(R.id.inputUserNameLayout);
        userPhoneLayout = (TextInputLayout) findViewById(R.id.inputUserPhoneNumberLayout);

        continueButton = (Button) findViewById(R.id.user_info_continue_button);
        resetButton = (Button) findViewById(R.id.reset_user_button);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteInputUserNameText.setText("");
                userPhoneNumber.setText("");
                userNameLayout.setError(null);
                userPhoneLayout.setError(null);
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allFieldsEntered = true;
                if (autoCompleteInputUserNameText.getText().toString().equals("")) {
                    userNameLayout.setError("Please enter name");
                    allFieldsEntered = false;
                }
                if (userPhoneNumber.getText().toString().equals("")) {
                    userPhoneLayout.setError("Please enter phone number");
                    userPhoneNumber.findFocus();
                    allFieldsEntered = false;
                }
                if(allFieldsEntered) {
                    try {
                        queryBuilder.reset();
                        queryBuilder.where().eq("phoneNumber",userPhoneNumber.getText().toString().trim());
                        userList = queryBuilder.query();
                        if(userList!=null && userList.size()>0) {
                            updateBuilder.reset();
                            updateBuilder.where().eq("phoneNumber",userPhoneNumber.getText().toString().trim());
                            updateBuilder.updateColumnValue("name",autoCompleteInputUserNameText.getText().toString().trim());
                            updateBuilder.update();
                        }
                        else {
                            User newUser = new User();
                            newUser.setName(autoCompleteInputUserNameText.getText().toString().trim());
                            newUser.setPhoneNumber(userPhoneNumber.getText().toString());
                            userDao.create(newUser);
                            //TODO Web service call to save new user in this particular outlet
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

}
