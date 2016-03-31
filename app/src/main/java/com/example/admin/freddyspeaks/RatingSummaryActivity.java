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
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RatingSummaryActivity extends BaseActivity {

    Dao<Question, Integer> questionDao;
    QueryBuilder<Question, Integer> questionQueryBuilder;

    PieChart ratingSummaryChart;

    Map<String,Question> questionMap = new HashMap<>();
    List<FeedbackResponse> feedbackResponseList;

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
        }
        catch(SQLException e) {
            Log.e("RatingSummaryActivity","Unable to fetch questions");
        }

    }

    public void populateDummyFeedback() {
        feedbackResponseList.add(new FeedbackResponse("1","1","Motish","7738657059",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","2","Bhupender","9876765654",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","4","Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","3","Kunal","9976754567",null,"1234","1200","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","4","Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","2","Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","3","Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","3","Kunal","9976754567",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","3","Motish","7738657059",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","2","Motish","7738657059",null,"1234","2500","abc"));
        feedbackResponseList.add(new FeedbackResponse("1","2","Motish","7738657059",null,"1234","2500","abc"));
    }

}
