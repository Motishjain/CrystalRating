package com.example.admin.util;

import android.util.Log;

import com.example.admin.database.SelectedReward;
import com.example.admin.database.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

/**
 * Created by Admin on 3/13/2016.
 */

public class RewardAllocationUtility {

    static int carryForwardAmount = 0;

    public static SelectedReward allocateReward(String userPhoneNumber, int billAmount, Dao<SelectedReward, Integer> selectedRewardDao, Dao<User, Integer> userDao) {

        int categoryAllocated = 0, targetAmount;
        try {
            QueryBuilder<User, Integer> userQueryBuilder = userDao.queryBuilder();
            userQueryBuilder.where().eq("phoneNumber", userPhoneNumber);
            User currentUser = userQueryBuilder.queryForFirst();
            carryForwardAmount = currentUser.getCarryForwardAmount();
        } catch (SQLException e) {
            Log.e("Reward Allocation","Unable to fetch user");
        }

        targetAmount = billAmount + carryForwardAmount;
        categoryAllocated = allocateCategory(targetAmount);

        if (categoryAllocated > 0 && selectedRewardDao != null) {
            return getReward(selectedRewardDao, categoryAllocated);
        } else {
            return null;
        }
    }

    public static void main(String args[]) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("D:/freddy/algo-analysis.txt", "UTF-8");
            for (int i = 1; i < 100; i++) {
                Random r = new Random();
                int low = 300;
                int high = 10000;
                int result = r.nextInt(high - low) + low;
                writer.append(i + "," + result + "," + (allocateCategory(result)));
                writer.println();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(writer!=null) {
                writer.close();
            }
        }


    }

    private static int allocateCategory(int targetAmount) {

        int categoryAllocated = 0;
        double bronzeRatio = 0, silverRatio = 0, goldRatio = 0;
        if (targetAmount >= 500 && targetAmount < 1000) {
            bronzeRatio = 15.0 + (targetAmount - 500) / 20.0;
        } else if (targetAmount >= 1000 && targetAmount < 3000) {
            //Chances are in order Bronze (40% or more), Silver (5% or less), Gold (0%)

            //40% + (max 40% of remaining depending on target amount)
            //ex: if target amount = 2000, bR = 40 + 20% of 60 = 52%
            bronzeRatio = 40.0 + (targetAmount - 1000) * 3.0 / 250.0;
            silverRatio = (float) (targetAmount - 1000) / 50.0 * (100.0 - bronzeRatio) / 100.0;

        } else if (targetAmount >= 3000 && targetAmount < 6000) {
            //Chances are in order Silver (40% or more), Bronze (7% or less), Gold (6% or less)
            silverRatio = 40;
            bronzeRatio = 24;
            goldRatio = 7;
            silverRatio += ((100.0 - (silverRatio + bronzeRatio + goldRatio)) * (float) (targetAmount - 3000) / 7500);
            goldRatio += ((100.0 - (silverRatio + bronzeRatio + goldRatio)) * (float) (targetAmount - 3000) / 7500);

            bronzeRatio = (100.0 - (silverRatio + goldRatio));
        } else {
            //Chances are in order Gold (40% or more), Silver (20% or less), Bronze (12% or less)
            goldRatio = 40;
            silverRatio = 24;
            bronzeRatio = 14;
            for (int i = targetAmount; i >= 7000; i -= 1000) {
                goldRatio += (100 - (silverRatio + bronzeRatio + goldRatio)) * 0.2;
                silverRatio += (100 - (silverRatio + bronzeRatio + goldRatio)) * 0.2;
                bronzeRatio += (100 - (silverRatio + bronzeRatio + goldRatio)) * 0.2;
            }
            int i = targetAmount % 1000;
            goldRatio += (100 - (silverRatio + bronzeRatio + goldRatio)) * 0.2 * i / 1000;
            silverRatio += (100 - (silverRatio + bronzeRatio + goldRatio)) * 0.2 * i / 1000;
            bronzeRatio += (100 - (silverRatio + bronzeRatio + goldRatio)) * 0.2 * i / 1000;

            silverRatio = (100.0 - (bronzeRatio + goldRatio));
        }

        Random randomGenerator = new SecureRandom();

        int randomNumber = randomGenerator.nextInt(100);

        if (randomNumber < bronzeRatio) {
            //Bronze allocated
            categoryAllocated = 1;
        } else if (randomNumber < (bronzeRatio + silverRatio)) {
            //Silver allocated
            categoryAllocated = 2;
        } else if (randomNumber < (bronzeRatio + silverRatio + goldRatio)) {
            //Gold allocated
            categoryAllocated = 3;
        }
        return categoryAllocated;
    }

    private static SelectedReward getReward(Dao<SelectedReward, Integer> selectedRewardDao, int categoryAllocated) {
        QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
        try {
            selectedRewardQueryBuilder.where().eq("rewardCategory", categoryAllocated);
            List<SelectedReward> possibleRewards = selectedRewardQueryBuilder.query();
            if (possibleRewards.size() > 0) {
                Random randomGenerator = new SecureRandom();
                int randomRewardIndex = randomGenerator.nextInt(possibleRewards.size());
                return possibleRewards.get(randomRewardIndex);
            } else if (categoryAllocated > 1) {
                return getReward(selectedRewardDao, categoryAllocated - 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}