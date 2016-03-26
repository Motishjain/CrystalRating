package com.example.admin.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "SELECTED_REWARD")
public class SelectedReward implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField (foreign = true)
    private Reward reward;

    @DatabaseField
    private String rewardCategory;

    private int carryForwardAmount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public String getRewardCategory() {
        return rewardCategory;
    }

    public void setRewardCategory(String rewardCategory) {
        this.rewardCategory = rewardCategory;
    }

    public int getCarryForwardAmount() {
        return carryForwardAmount;
    }

    public void setCarryForwardAmount(int carryForwardAmount) {
        this.carryForwardAmount = carryForwardAmount;
    }
}
