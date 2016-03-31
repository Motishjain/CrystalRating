package com.example.admin.freddyspeaks;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.freddyspeaks.R;
import com.example.admin.webservice.response_objects.FeedbackResponse;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RatingSummaryActivity extends BaseActivity {

    Dao<Question, Integer> questionDao;
    QueryBuilder<Question, Integer> questionQueryBuilder;

    PieChart ratingSummaryChart;

    Map<String,Question> questionMap = new HashMap<>();
    List<FeedbackResponse> feedbackResponseList;
    Question selectedQuestion;
    Map<Integer,List<Integer>> ratingWiseFeedbackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_summary);
        ratingSummaryChart = (PieChart) findViewById(R.id.ratingSummaryChart);
        feedbackResponseList = new ArrayList<>();
        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            List<Question> questionList = questionQueryBuilder.query();
            for(Question question:questionList) {
                questionMap.put(question.getQuestionId(),question);
            }
            selectedQuestion = questionMap.get("0");


        }
        catch(SQLException e) {
            Log.e("RatingSummaryActivity","Unable to fetch questions");
        }

    }

    public void populateDummyFeedback() {
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Motish","7738657059",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Bhupender","9876765654",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Kunal","9976754567",null,"1234","1200","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(), "Kunal", "9976754567", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(), "Motish", "7738657059", null, "1234", "2500", "abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Motish","7738657059",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse(createMap(),"Motish","7738657059",null,"1234","2500","abc"));
    }

    public Map<String,String> createMap(){
        Map<String,String> ratingMap = new HashMap<>();
        Random randomGenerator = new SecureRandom();
        int randomNumber = randomGenerator.nextInt(4);
        ratingMap.put("1",(randomNumber+1)+"");
        return ratingMap;
    }

    public void refreshPieChart() {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<String>();
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

        int optionIndex = 0;

        for(String option:options) {
            Integer count = ratingWiseFeedbackList.get(option)==null?0:ratingWiseFeedbackList.get(option).size();
            entries.add(new Entry(count,optionIndex));
            optionIndex++;
        }

        PieDataSet dataset = new PieDataSet(entries, "# of Calls");
        PieData data = new PieData(labels, dataset);
        ratingSummaryChart.setData(data);
    }

}
