package com.admin.webservice.request_objects;

import java.util.Date;

/**
 * Created by Admin on 21-05-2016.
 */
public class ExtendSubscriptionRequest {

    private String outletCode;
    private String paymentId;
    private String amount;
    private String subscribedMonths;
    private Date paymentDate;

    public String getOutletCode() {
        return outletCode;
    }

    public void setOutletCode(String outletCode) {
        this.outletCode = outletCode;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSubscribedMonths() {
        return subscribedMonths;
    }

    public void setSubscribedMonths(String subscribedMonths) {
        this.subscribedMonths = subscribedMonths;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }
}
