package com.admin.webservice.response_objects;

/**
 * Created by Admin on 30-04-2016.
 */
public class SubscriptionResponse {

    private String outletCode;

    private String expiryDate;

    private String activationStatus;

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
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
}
