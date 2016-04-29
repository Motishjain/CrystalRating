package com.admin.webservice.response_objects;

/**
 * Created by Admin on 24-04-2016.
 */
public class DailySaleResponse {

    private Integer dayOfMonth;
    private String totalSale;

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(String totalSale) {
        this.totalSale = totalSale;
    }
}
