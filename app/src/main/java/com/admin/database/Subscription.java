package com.admin.database;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Admin on 30-04-2016.
 */
public class Subscription {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String expiryDate;

    @DatabaseField
    private String activationStatus;

    @DatabaseField
    private Integer daysRemaining;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(String activationStatus) {
        this.activationStatus = activationStatus;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
}
