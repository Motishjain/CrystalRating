package com.admin.util;

import android.util.Log;

import com.admin.constants.AppConstants;
import com.admin.database.SelectedReward;
import com.admin.database.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Admin on 3/13/2016.
 */

public class RewardAllocationUtility {

    static int carryForwardAmount = 0;
    public static Map<Integer,String> categoryMapping = new HashMap<>();
    static {
        categoryMapping.put(1, AppConstants.BRONZE_CD);
        categoryMapping.put(2, AppConstants.SILVER_CD);
        categoryMapping.put(3, AppConstants.GOLD_CD);
    }

    public static final int REWARD_AMOUNT_THRESHOLD_1 = 500;

    public static final int REWARD_AMOUNT_THRESHOLD_2 = 3000;

    public static final int REWARD_AMOUNT_THRESHOLD_3 = 6000;

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
            for (int i = 1; i < 500; i++) {
                Random r = new Random();
                int low = 200;
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

    private static int allocateCategory(int targetAmount) {//target amt + carry forward amt


        int categoryAllocated = 0;
        double bronzeRatio = 0, silverRatio = 0, goldRatio = 0;
        if (targetAmount >= REWARD_AMOUNT_THRESHOLD_1 && targetAmount < REWARD_AMOUNT_THRESHOLD_2) {
            //Chances are in order Bronze (50% and more), Silver (5% or less), Gold (0%)

            //Bronze is 50% + (max 50% of remaining depending on target amount)
            //ex: if target amount = 2000, bR = 50 + 30% of 50 = 65%
            bronzeRatio = 50.0 + (targetAmount - 500) * 1.0 / 100.0;
            silverRatio = (float) (targetAmount - 500) / 100.0 * (100.0 - bronzeRatio) / 100.0;

        } else if (targetAmount >= REWARD_AMOUNT_THRESHOLD_2 && targetAmount < REWARD_AMOUNT_THRESHOLD_3) {
            //Chances are in order Silver (40% or more), Bronze (7% or less), Gold (6% or less)
            silverRatio = 40;
            bronzeRatio = 24;
            goldRatio = 7;
            silverRatio += ((100.0 - (silverRatio + bronzeRatio + goldRatio)) * (float) (targetAmount - REWARD_AMOUNT_THRESHOLD_2) / 7500);
            goldRatio += ((100.0 - (silverRatio + bronzeRatio + goldRatio)) * (float) (targetAmount - REWARD_AMOUNT_THRESHOLD_2) / 7500);

            bronzeRatio = (100.0 - (silverRatio + goldRatio));
        } else if (targetAmount >= REWARD_AMOUNT_THRESHOLD_3) {
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
            if(targetAmount < REWARD_AMOUNT_THRESHOLD_1)
                carryForwardAmount = 0;
            else
                carryForwardAmount = targetAmount - REWARD_AMOUNT_THRESHOLD_1;

        } else if (randomNumber < (bronzeRatio + silverRatio)) {
            //Silver allocated
            categoryAllocated = 2;
            if(targetAmount < REWARD_AMOUNT_THRESHOLD_2)
                carryForwardAmount = 0;
            else
                carryForwardAmount = targetAmount - REWARD_AMOUNT_THRESHOLD_2;

        } else if (randomNumber < (bronzeRatio + silverRatio + goldRatio)) {
            //Gold allocated
            categoryAllocated = 3;
            if(targetAmount < REWARD_AMOUNT_THRESHOLD_3)
                carryForwardAmount = 0;
            else
                carryForwardAmount = targetAmount - REWARD_AMOUNT_THRESHOLD_3;
        }
        return categoryAllocated;
    }

    private static SelectedReward getReward(Dao<SelectedReward, Integer> selectedRewardDao, int categoryAllocated) {
        QueryBuilder<SelectedReward, Integer> selectedRewardQueryBuilder = selectedRewardDao.queryBuilder();
        try {
            selectedRewardQueryBuilder.where().eq("rewardCategory", categoryMapping.get(categoryAllocated));
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