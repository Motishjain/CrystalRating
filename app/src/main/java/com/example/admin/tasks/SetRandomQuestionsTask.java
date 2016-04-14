package com.example.admin.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.example.admin.constants.AppConstants;
import com.example.admin.database.DBHelper;
import com.example.admin.database.Question;
import com.example.admin.database.Reward;
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
public class SetRandomQuestionsTask extends AsyncTask<Void, Void, Void> {

    Context context;
    CallBackListener callBackListener;

    public SetRandomQuestionsTask(Context context, CallBackListener callBackListener) {
        this.context = context;
        this.callBackListener = callBackListener;
    }

    @Override
    protected Void doInBackground(Void... rewards) {
        setRandomQuestions(context);
        return null;
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
            if (questionList.size() == 0) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Question question : questionList) {
            if (question.getQuestionType().equals(AppConstants.PRODUCT_QUESTION_TYPE)) {
                productQuestionList.add(question);
            } else if (question.getQuestionType().equals(AppConstants.SERVICE_QUESTION_TYPE)) {
                serviceQuestionList.add(question);
            } else if (question.getQuestionType().equals(AppConstants.MISCELLENOUS_QUESTION_TYPE)) {
                miscQuestionList.add(question);
            }
        }

        Random randomGenerator = new SecureRandom();
        questionList.clear();
        if (productQuestionList.size() > 0) {
            questionList.add(productQuestionList.get(randomGenerator.nextInt(productQuestionList.size())));
        }
        if (serviceQuestionList.size() > 0) {
            questionList.add(serviceQuestionList.get(randomGenerator.nextInt(serviceQuestionList.size())));
        }
        if (miscQuestionList.size() > 0) {
            questionList.add(miscQuestionList.get(randomGenerator.nextInt(miscQuestionList.size())));
        }

        try {
            questionUpdateBuilder = questionDao.updateBuilder();

            String selectedQuestionIds[] = new String[questionList.size()];
            int count = 0;
            //Select 3 questions
            for (Question question : questionList) {
                selectedQuestionIds[count++] = question.getQuestionId();
            }
            questionUpdateBuilder.where().in("questionId", selectedQuestionIds);
            questionUpdateBuilder.updateColumnValue("selected", "Y");
            questionUpdateBuilder.update();

            questionUpdateBuilder.reset();

            questionUpdateBuilder.where().notIn("questionId", selectedQuestionIds);
            questionUpdateBuilder.updateColumnValue("selected", "N");
            questionUpdateBuilder.update();
            if(callBackListener!=null) {
                callBackListener.onTaskCompleted();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public interface CallBackListener {
        void onTaskCompleted();
    }
}
