package com.example.mjai37.database;

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

    @DatabaseField
    private String rewardId;

    @DatabaseField
    private Integer rewardCategory;

    @DatabaseField
    private String cost;

    @DatabaseField(dataType = DataType.STRING_BYTES)
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public Integer getRewardCategory() {
        return rewardCategory;
    }

    public void setRewardCategory(Integer rewardCategory) {
        this.rewardCategory = rewardCategory;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
