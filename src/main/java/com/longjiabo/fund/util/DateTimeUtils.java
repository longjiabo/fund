package com.longjiabo.fund.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date parseDate(String start, String s) {
        SimpleDateFormat sdf = new SimpleDateFormat(s);
        try {
            return sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
