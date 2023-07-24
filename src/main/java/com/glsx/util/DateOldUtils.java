package com.glsx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateOldUtils {
//    public static String Sdf1ToSdf2() {
//
//    }

    public final static SimpleDateFormat SDF1 = new SimpleDateFormat("yyyyMMddHHmmss");
    public final static SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // yyyyMMddHHmmss 日期增加year年
    public static String convertTime1(String time, int year) {
        try {
            SimpleDateFormat SDF1 = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SDF1.parse(time));
            calendar.add(Calendar.YEAR, year);
            return SDF1.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    // yyyyMMddHHmmss 日期增加year年
    public static String convertTime1(String time, int year, int mon) {
        try {
            SimpleDateFormat SDF1 = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SDF1.parse(time));
            calendar.add(Calendar.YEAR, year);
            calendar.add(Calendar.MONTH, mon);
            return SDF1.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }




    // yyyy-MM-dd HH:mm:ss
    public static String convertTime2(String time, int year) {
        try {
            SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SDF2.parse(time));
            calendar.add(Calendar.YEAR, year);
            return SDF2.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    // yyyy-MM-dd HH:mm:ss
    public static String convertTime2(String time, int year, int mon) {
        try {
            SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SDF2.parse(time));
            calendar.add(Calendar.YEAR, year);
            calendar.add(Calendar.MONTH, mon);
            return SDF2.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }





    // yyyyMMddHHmmss --> yyyy-MM-dd HH:mm:ss
    public static String date1ToDate2(String time) {
        try {
            SimpleDateFormat SDF1 = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return SDF2.format(SDF1.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void main(String[] args) throws ParseException {
        SimpleDateFormat SDF1 = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String time1 = "20201026113900";
        String time2 = "2018-10-26 11:39:00";

//        System.out.println(SDF1.format(SDF1.parse(time1)));
//        System.out.println(SDF2.format(SDF1.parse(time1)));
//
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.YEAR, -1);
//
//        Date time = calendar.getTime();
//        System.out.println(SDF2.format(time));

        System.out.println(convertTime1(time1,-1, 2));
        System.out.println(convertTime2(time2,-2));

//        System.out.println(date1ToDate2("20201026113900"));
    }
}
