package com.example.admin.webservice.request_objects;

import java.sql.Timestamp;

/**
 * Created by Admin on 3/31/2016.
 */
public class FeedbackSummaryRequest {

    private String outletCode;

    private Timestamp fromDate;

    private Timestamp toDate;

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public Timestamp getFromDate() {
        return fromDate;
    }

    public void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    public Timestamp getToDate() {
        return toDate;
    }

    public void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

}
