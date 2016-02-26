package com.example.mjai37.freddyspeaks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.Outlet;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class RegisterOutletActivity extends AppCompatActivity {

    EditText outletName, alias, addrLine1, addrLine2, pinCode, email, phoneNumber;
    Button nextButton;
    Dao<Outlet, Integer> outletDao;


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
        nextButton = (Button) findViewById(R.id.next_button);

        try {
            outletDao = OpenHelperManager.getHelper(this, DBHelper.class).getOutletDao();
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
                newOutlet.setPhoneNumber(phoneNumber.getText().toString());
                try {
                    outletDao.create(newOutlet);
                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO logging
                }
                Intent homePage = new Intent(RegisterOutletActivity.this, HomePageActivity.class);
                startActivity(homePage);
                //TODO web service call to save outlet info
            }
        });
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
