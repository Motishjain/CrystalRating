package com.admin.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Admin on 18-06-2016.
 */
public class DateTimeUtility {

    public static Date getLocalDate() {
        Calendar calendar = new GregorianCalendar();
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Calcutta");
        calendar.setTimeZone(timeZone);
        return calendar.getTime();
    }
}
