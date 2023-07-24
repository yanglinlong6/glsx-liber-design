package com.glsx.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {
    public static final String DATE_FORMAT1 = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_ORDER = "yyyyMMdd";
    public static final String DEFAULT_YYMM_FORMAT = "yyyy-MM";

    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String YMDHMSS = "yyyy-MM-dd HH:mm:ss:sss";
    public static final String YMDHMS = "yyyyMMddHHmmss";

    public final static SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy-MM-dd");

    public final static SimpleDateFormat SDF4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    //
    public static String difference() {


        return null;
    }

    // yyyy-MM-dd
    public static String getYesterday() {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        return SDF1.format(cal.getTime());
    }

    public static String getToday() {
        Calendar cal=Calendar.getInstance();
        return SDF4.format(cal.getTime());
    }


    public static void main(String[] args) {

    }
}
