package com.admin.webservice.response_objects;

/**
 * Created by Admin on 24-04-2016.
 */
public class DailySaleResponse {

    private String saleDate;
    private double totalAmount;

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
