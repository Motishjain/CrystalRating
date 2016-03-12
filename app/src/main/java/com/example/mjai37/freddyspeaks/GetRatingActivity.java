package com.example.mjai37.freddyspeaks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.mjai37.database.DBHelper;
import com.example.mjai37.database.Feedback;
import com.example.mjai37.database.Question;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.util.List;

import layout.RatingCardFragment;

public class GetRatingActivity extends AppCompatActivity {


    Dao<Question, Integer> questionDao;

    QueryBuilder<Question, Integer> queryBuilder;
    UpdateBuilder<Feedback, Integer> updateBuilder;
    List<Question> questionList;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            questionDao = OpenHelperManager.getHelper(this, DBHelper.class).getQuestionDao();
            queryBuilder = questionDao.queryBuilder();
            queryBuilder.where().eq("selected","Y");
            questionList = queryBuilder.query();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Question question = questionList.get(0);
        RatingCardFragment fragment = RatingCardFragment.newInstance(question.getName(),question.getRatingValues().split(","));
        fragmentTransaction.add(R.id.ratingCardHolder, fragment);
        fragmentTransaction.commit();

    }

}
