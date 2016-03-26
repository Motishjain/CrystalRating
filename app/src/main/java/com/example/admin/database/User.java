package com.example.admin.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mjai37 on 1/21/2016.
 */

@DatabaseTable(tableName = "USER")
public class User implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private int carryForwardAmount;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCarryForwardAmount() {
        return carryForwardAmount;
    }

    public void setCarryForwardAmount(int carryForwardAmount) {
        this.carryForwardAmount = carryForwardAmount;
    }
}