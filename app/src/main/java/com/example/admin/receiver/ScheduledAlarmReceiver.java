package com.example.admin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Admin on 4/6/2016.
 */
public class ScheduledAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setRandomQuestions(context);
    }

    private void setRandomQuestions(Context context) {

        Dao<Question, Integer> questionDao = null;
        QueryBuilder<Question, Integer> questionQueryBuilder;
        UpdateBuilder<Question, Integer> questionUpdateBuilder;
        List<Question> productQuestionList = new ArrayList<>();
        List<Question> serviceQuestionList = new ArrayList<>();
        List<Question> miscQuestionList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>();

        try {
            questionDao = OpenHelperManager.getHelper(context, DBHelper.class).getCustomDao("Question");
            questionQueryBuilder = questionDao.queryBuilder();
            questionList = questionQueryBuilder.query();

        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Question question:questionList) {
            if(question.getQuestionType().equals(AppConstants.PRODUCT_QUESTION_TYPE)) {
                productQuestionList.add(question);
            }
            else if(question.getQuestionType().equals(AppConstants.SERVICE_QUESTION_TYPE)) {
                serviceQuestionList.add(question);
            }
            else if(question.getQuestionType().equals(AppConstants.MISCELLENOUS_QUESTION_TYPE)) {
                miscQuestionList.add(question);
            }
        }

        Random randomGenerator = new SecureRandom();
        questionList.clear();
        questionList.add(productQuestionList.get(randomGenerator.nextInt(productQuestionList.size())));
        questionList.add(serviceQuestionList.get(randomGenerator.nextInt(serviceQuestionList.size())));
        questionList.add(miscQuestionList.get(randomGenerator.nextInt(miscQuestionList.size())));

        try {
            questionUpdateBuilder = questionDao.updateBuilder();

            //Select 3 questions
            for(Question question:questionList) {
                questionUpdateBuilder.where().or().eq("questionId", question.getQuestionId());
            }
            questionUpdateBuilder.updateColumnValue("selected","Y");
            questionUpdateBuilder.update();

            questionUpdateBuilder.reset();

            //Deselect other questions
            for(Question question:questionList) {
                questionUpdateBuilder.where().or().ne("questionId", question.getQuestionId());
            }
            questionUpdateBuilder.updateColumnValue("selected","N");
            questionUpdateBuilder.update();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
