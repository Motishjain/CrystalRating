package com.example.admin.freddyspeaks;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.webservice.RestEndpointInterface;
import com.example.admin.webservice.RetrofitSingleton;
import com.example.admin.webservice.response_objects.FeedbackResponse;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingSummaryActivity extends BaseActivity {

    Dao<Question, Integer> questionDao;
    QueryBuilder<Question, Integer> questionQueryBuilder;
    String outletCode;

    PieChart ratingSummaryChart;
    TextView fromDateTextView,toDateTextView;
    Spinner questionsSpinner;

    Map<String,Question> questionMap = new HashMap<>();
    List<Question> answeredQuestionList = new ArrayList<>();
    List<FeedbackResponse> feedbackResponseList;
    Question selectedQuestion;
    Map<Integer,List<Integer>> ratingWiseFeedbackList;
    Typeface textFont;

    Date fromDate, toDate;
    SimpleDateFormat simpleDateFormat;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_summary);
        ratingSummaryChart = (PieChart) findViewById(R.id.ratingSummaryChart);
        fromDateTextView = (TextView) findViewById(R.id.fromDate);
        toDateTextView = (TextView) findViewById(R.id.toDate);
        questionsSpinner = (Spinner) findViewById(R.id.questionsSpinner);
        questionsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Rating summary","Item selected");
                selectedQuestion = answeredQuestionList.get(position);
                refreshPieChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Rating summary","Nothing selected");
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        outletCode = sharedPreferences.getString("outletCode", null) ;

        simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");

        //Set to date for feedback
        calendar = Calendar.getInstance();
        toDate = calendar.getTime();
        setDateTextView(toDateTextView, toDate);

        //Set from date for feedback
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        fromDate = calendar.getTime();
        setDateTextView(fromDateTextView, fromDate);

        ratingSummaryChart.setDrawHoleEnabled(false);
        ratingSummaryChart.setUsePercentValues(false);
        Legend legend = ratingSummaryChart.getLegend();
        textFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/comicsansms.ttf");
        legend.setTypeface(textFont);
        legend.setTextSize(10);

        ratingSummaryChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //TODO open popup to show details
            }

            @Override
            public void onNothingSelected() {

            }
        });
        feedbackResponseList = new ArrayList<>();
        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            List<Question> questionList = questionQueryBuilder.query();
            for(Question question:questionList) {
                //TODO remove stub
                answeredQuestionList.add(question);

                questionMap.put(question.getQuestionId(),question);
            }

            //TODO remove stub
            populateDummyFeedback();

            //fetchFeedback();
        }
        catch(SQLException e) {
            Log.e("RatingSummaryActivity","Unable to fetch questions");
        }

    }

    public void fetchFeedback () {
        RestEndpointInterface restEndpointInterface = RetrofitSingleton.newInstance();
        Call<List<FeedbackResponse>> fetchRewardsCall = restEndpointInterface.fetchFeedback(fromDateTextView.getText().toString(), toDateTextView.getText().toString(), outletCode);
        fetchRewardsCall.enqueue(new Callback<List<FeedbackResponse>>() {
            @Override
            public void onResponse(Call<List<FeedbackResponse>> call, Response<List<FeedbackResponse>> response) {
                if (response.isSuccess()) {
                    feedbackResponseList = response.body();
                    populateAnsweredQuestionsList();
                }
            }

            @Override
            public void onFailure(Call<List<FeedbackResponse>> call, Throwable t) {
                //TODO handle failure
            }
        });
    }

    public void populateAnsweredQuestionsList() {
        answeredQuestionList.clear();
        Set<String> questionIdSet = new HashSet<>();
        List<String> questionNames = new ArrayList<>();

        for(FeedbackResponse feedbackResponse:feedbackResponseList) {
            questionIdSet.addAll(feedbackResponse.getRatingsMap().keySet());
        }

        for(String questionId: questionIdSet) {
            answeredQuestionList.add(questionMap.get(questionId));
            questionNames.add(questionMap.get(questionId).getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, questionNames);
        questionsSpinner.setAdapter(dataAdapter);
    }

    public void refreshPieChart() {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        String[] options = selectedQuestion.getRatingValues().split(",");
        ratingWiseFeedbackList = new HashMap<>();
        int feedbackIndex = 0;

        for(String option : options) {
            labels.add(option);
        }

        for(FeedbackResponse feedbackResponse:feedbackResponseList) {
            Integer selectedOption = feedbackResponse.getRatingsMap().get(selectedQuestion.getQuestionId());
            if(selectedOption!=null) {
                if(ratingWiseFeedbackList.get(selectedOption)==null){
                    ratingWiseFeedbackList.put(selectedOption, new ArrayList<Integer>());
                }
                ratingWiseFeedbackList.get(selectedOption).add(feedbackIndex);
            }
            feedbackIndex++;
        }

        for(int optionIndex=0;optionIndex<options.length;optionIndex++) {
            Integer count = ratingWiseFeedbackList.get(optionIndex+1)==null?0:ratingWiseFeedbackList.get(optionIndex+1).size();
            entries.add(new Entry(count,optionIndex));
        }

        PieDataSet dataset = new PieDataSet(entries, "Ratings");
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        dataset.setValueTextSize(12);
        dataset.setValueTypeface(textFont);
        PieData data = new PieData(labels, dataset);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int intValue = (int) value;
                return intValue + " customers rated";
            }
        });
        ratingSummaryChart.setData(data);
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
                        calendar.set(Calendar.MONTH,monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        if(calendar.getTime().after(toDate)) {
                            //TODO alert dialogue
                            return;
                        }
                        if(!fromDate.equals(calendar.getTime())) {
                            fetchFeedback();
                        }
                        fromDate = calendar.getTime();
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
                        calendar.set(Calendar.MONTH,monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        if(calendar.getTime().before(fromDate)) {
                            //TODO alert dialogue
                            return;
                        }
                        if(!toDate.equals(calendar.getTime())) {
                            fetchFeedback();
                        }
                        toDate = calendar.getTime();
                        setDateTextView(toDateTextView, toDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        toDatePickerDialog.setTitle("Select To Date");
        toDatePickerDialog.show();
    }

    public void setDateTextView(TextView textView,Date date) {
        textView.setText(simpleDateFormat.format(date));
    }

    //TODO remove stub
    public void populateDummyFeedback() {
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(),"Motish","7738657059",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Bhupender", "9876765654", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Kunal", "9976754567", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(),"Kunal","9976754567",null,"1234","1200","abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Kunal", "9976754567", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Kunal", "9976754567", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(),"Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Kunal", "9976754567", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Motish", "7738657059", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Motish", "7738657059", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createRatingsMap(), "Motish", "7738657059", null, "1234", "2500", "abc"));
    }

    //TODO remove stub
    public Map<String,Integer> createRatingsMap(){
        Map<String,Integer> ratingMap = new HashMap<>();
        Random randomGenerator = new SecureRandom();
        int randomNumber = randomGenerator.nextInt(4);
        ratingMap.put(selectedQuestion.getQuestionId(), (randomNumber + 1));
        return ratingMap;
    }
}