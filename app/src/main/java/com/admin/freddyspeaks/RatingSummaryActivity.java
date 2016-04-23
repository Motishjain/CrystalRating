package com.admin.freddyspeaks;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.admin.tasks.FetchAndFragmentFeedbackTask;
import com.admin.view.CustomProgressDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RatingSummaryActivity extends BaseActivity {

    TextView fromDateTextView, toDateTextView;
    ProgressDialog progressDialog;

    Date fromDate, toDate;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_summary);

        fromDateTextView = (TextView) findViewById(R.id.fromDate);
        toDateTextView = (TextView) findViewById(R.id.toDate);

        simpleDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        //Set to date for feedback
        calendar = Calendar.getInstance();
        toDate = calendar.getTime();
        setDateTextView(toDateTextView, toDate);

        //Set from date for feedback
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        fromDate = calendar.getTime();
        setDateTextView(fromDateTextView, fromDate);

        progressDialog = CustomProgressDialog.createCustomProgressDialog(this);
        progressDialog.show();
        FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask(fromDate, toDate, progressDialog);
        fetchAndFragmentFeedbackTask.execute(this);
    }

    public void changeFromDate(View v) {
        calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        DatePickerDialog fromDatePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.getTime().after(toDate)) {
                            //TODO alert dialogue
                            return;
                        }
                        if (!fromDate.equals(calendar.getTime())) {
                            fromDate = calendar.getTime();
                            FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask(fromDate,toDate, progressDialog);
                            fetchAndFragmentFeedbackTask.execute(RatingSummaryActivity.this);
                        }
                        setDateTextView(fromDateTextView, fromDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.setTitle("Select From Date");
        fromDatePickerDialog.show();
    }

    public void changeToDate(View v) {
        calendar = Calendar.getInstance();
        calendar.setTime(toDate);
        DatePickerDialog toDatePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        if (calendar.getTime().before(fromDate)) {
                            //TODO alert dialogue
                            return;
                        }
                        if (!toDate.equals(calendar.getTime())) {
                            toDate = calendar.getTime();
                            FetchAndFragmentFeedbackTask fetchAndFragmentFeedbackTask = new FetchAndFragmentFeedbackTask(fromDate,toDate, progressDialog);
                            fetchAndFragmentFeedbackTask.execute(RatingSummaryActivity.this);
                        }
                        setDateTextView(toDateTextView, toDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog.setTitle("Select To Date");
        toDatePickerDialog.show();
    }

    public void setDateTextView(TextView textView, Date date) {
        textView.setText(simpleDateFormat.format(date));
    }

    public void closeActivity(View v) {
        this.finish();
    }
}