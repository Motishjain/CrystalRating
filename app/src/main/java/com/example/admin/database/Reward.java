package com.example.admin.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "REWARD")
public class Reward implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String level;

    @DatabaseField
    private String rewardType;

    @DatabaseField
    private String cost;

    @DatabaseField
    private String image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
