package com.longjiabo.fund.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BaseUtils {
    private static final Logger log = LoggerFactory.getLogger(BaseUtils.class);

    public static boolean oneDay(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }


    public static boolean needProxy() {
        return System.getProperty("os.name").toUpperCase().contains("MAC");
    }

    public static String parseTransactionType(Integer type) {
        switch (type) {
            case 1:
                return "买入";
            case 2:
                return "卖出";
            case 3:
                return "现金分红";
            case 5:
                return "红利再投";
            default:
                return "-";
        }

    }

    public static Double formatDouble(Double t, String pattern) {
        if (pattern == null)
            pattern = "#.##";
        //final String pattern = "###,###,##0.00";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        return Double.valueOf(myFormatter.format(t));
    }

    public static List<Double> formatDouble(List<Double> t, String pattern) {
        if (pattern == null)
            pattern = "#.##";
        //final String pattern = "###,###,##0.00";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        List<Double> ts = new ArrayList<>();
        for (Double d : t) {
            ts.add(Double.valueOf(myFormatter.format(d)));
        }
        return ts;
    }
}
