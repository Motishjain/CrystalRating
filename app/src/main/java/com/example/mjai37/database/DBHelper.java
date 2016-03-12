package com.example.mjai37.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mjai37.freddyspeaks.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by mjai37 on 1/21/2016.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {


    private static final String DATABASE_NAME = "Outlets.db";
    private static final int DATABASE_VERSION = 1;
    private Dao<Outlet, Integer> outletDao;
    private Dao<User, Integer> userDao;
    private Dao<Reward, Integer> rewardDao;
    private Dao<Question, Integer> questionDao;
    private Dao<GoodieHistory, Integer> goodieHistoryDao;
    private Dao<Feedback, Integer> feedbackDao;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            // Create tables. This onCreate() method will be invoked only once of the application life time i.e. the first time when the application starts.
            TableUtils.createTable(connectionSource, Outlet.class);
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Question.class);
            TableUtils.createTable(connectionSource, Reward.class);
            TableUtils.createTable(connectionSource, Feedback.class);
            TableUtils.createTable(connectionSource, GoodieHistory.class);

        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Unable to create datbases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            // In case of change in database of next version of application, please increase the value of DATABASE_VERSION variable, then this method will be invoked
            //automatically. Developer needs to handle the upgrade logic here, i.e. create a new table or a new column to an existing table, take the backups of the
            // existing database etc.
            TableUtils.dropTable(connectionSource, Outlet.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(DBHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer, e);
        }
    }

    // Create the getDao methods of all database tables to access those from android code.
    // Insert, delete, read, update everything will be happened through DAOs

    public Dao<Outlet, Integer> getOutletDao() throws SQLException {
        if (outletDao == null) {
            outletDao = getDao(Outlet.class);
        }
        return outletDao;
    }

    public Dao<User, Integer> getUserDao() throws SQLException {
        if (userDao == null) {
            userDao = getDao(User.class);
        }
        return userDao;
    }

    public Dao<Reward, Integer> getRewardDao() throws SQLException {
        if (rewardDao == null) {
            rewardDao = getDao(Reward.class);
        }
        return rewardDao;
    }

    public Dao<Question, Integer> getQuestionDao() throws SQLException {
        if (questionDao == null) {
            questionDao = getDao(Question.class);
        }
        return questionDao;
    }

    public Dao<GoodieHistory, Integer> getGoodieHistoryDao() throws SQLException {
        if (goodieHistoryDao == null) {
            goodieHistoryDao = getDao(GoodieHistory.class);
        }
        return goodieHistoryDao;
    }

    public Dao<Feedback, Integer> getFeedbackDao() throws SQLException {
        if (feedbackDao == null) {
            feedbackDao = getDao(Feedback.class);
        }
        return feedbackDao;
    }


}