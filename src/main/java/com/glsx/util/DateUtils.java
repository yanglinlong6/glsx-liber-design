package com.glsx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtils {
    public final static SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy-MM-dd");
    public final static SimpleDateFormat SDF2 = new SimpleDateFormat("yyyyMM");
    public final static SimpleDateFormat SDF3 = new SimpleDateFormat("yyyyMMdd");

    public final static SimpleDateFormat SDF4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static SimpleDateFormat SDF5 = new SimpleDateFormat("yyyy-MM");

    // yyyy-MM-dd HH:mm:ss  -- > yyyy-MM
    public static String convTime1(String time) {
        try {
            return SDF5.format(SDF4.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // yyyy-MM-dd HH:mm:ss  -- > yyyy-MM-dd
    public static String convTime2(String time) {
        try {
            return SDF1.format(SDF4.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // yyyy-MM-dd HH:mm:ss  -- > yyyyMM
    public static String convTime3(String time) {
        try {
            return SDF2.format(SDF4.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // yyyy-MM-dd HH:mm:ss
    public static String convTime4() {
        Random random = new Random();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -(random.nextInt(150) + 30));
        cal.add(Calendar.MINUTE, random.nextInt(180));
        return SDF4.format(cal.getTime());
    }


    // yyyy-MM-dd HH:mm:ss  -- > yyyy-MM
    public static String convRandomMonth(String time) {
        try {
            Random random = new Random();
            Date parse = SDF4.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parse);
            cal.add(Calendar.DATE, -(random.nextInt(90) + 10));
            return SDF5.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 时间相差分钟
    public static int diffMinute(String time1, String time2) {
        try {
            long l1 = SDF4.parse(time1).getTime();
            long l2 = SDF4.parse(time2).getTime();

            return (int) ((l2-l1)/(1000*60));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 往前推算60天 yyyy-MM-dd
    public static List<String> getBeforeTenDay(String time) {
        try {
            ArrayList<String> timeList = new ArrayList<String>();

            Date parse = SDF1.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parse);

            String tmpTime = "";
            for (int i = 0; i < 60; i++) {
                cal.setTime(parse);
                cal.add(Calendar.DATE, -i);
                timeList.add(SDF1.format(cal.getTime()));
            }

            return timeList;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    // yyyy-MM-dd
    public static String getYesterday() {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        return SDF1.format(cal.getTime());
    }

    // yyyy-MM-dd
    public static String getToday() {
        Calendar cal=Calendar.getInstance();
        return SDF3.format(cal.getTime());
    }



    // yyyy-MM-dd
    public static String getBeforeYesterday() {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-2);
        return SDF1.format(cal.getTime());
    }

    // yyyy-MM-dd
    public static String getLastMonth() {
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MONTH,-1);
        return SDF1.format(cal.getTime());
    }

    // yyyy-MM
    public static String getAfterMonth(String time) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(SDF5.parse(time));
            cal.add(Calendar.MONTH, 1);
            return SDF5.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // yyyy-MM-dd HH:mm:ss --> yyyy-MM
    public static String getAfterMonth2(String time) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(SDF4.parse(time));
            cal.add(Calendar.MONTH, 1);
            return SDF5.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // yyyy-MM --> yyyy-MM
    public static String getAfterMonth3(String time) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(SDF5.parse(time));
            cal.add(Calendar.MONTH, 1);
            return SDF5.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    // yyyy-MM
    public static String getAfterDayMonth(String time) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(SDF4.parse(time));
            cal.add(Calendar.DATE, 1);
            return SDF5.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取指定时间往后推最近六个月日期 yyyyMM
     * @param
     */

    public static List<String> getAfterSixMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF2.parse(time);
            Calendar cal = Calendar.getInstance();

            int day = new Random().nextInt(15) % (15 - 7 + 1) + 7;
            cal.setTime(parse);
            cal.add(Calendar.DATE, day);
            list.add(SDF3.format(cal.getTime()));

            for (int i = 0; i < 5; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, i+1);
                list.add(SDF3.format(cal.getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 获取指定时间往前推最近六个月日期 yyyyMM
     * @param
     */

    public static List<String> getBeforeSixMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF2.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 6; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, -i);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 获取指定时间往前推最1-2个月日期 yyyyMM
    public static List<String> getBefore1st2stMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF2.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 2; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, -i-1);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 获取指定时间往前推最1~10个月日期 yyyyMM
    public static List<String> getBefore1To10stMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF2.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 10; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, -i-1);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }


    // 获取指定时间往前推最3-4个月日期 yyyyMM
    public static List<String> getBefore3st4stMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF2.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 2; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, -i-3);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }


    // 获取指定时间往后推最1-2个月日期 yyyy-MM
    public static List<String> getAfter1st2stMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF5.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 2; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, i+1);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    // 获取指定时间往后推最1-6个月日期 yyyy-MM
    public static List<String> getAfter126tMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF5.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 6; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, i+1);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }


    // 获取指定时间往后推最1-10个月日期 yyyy-MM
    public static List<String> getAfterTenMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF5.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 10; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, i);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }


    // 获取指定时间往前推最1-10个月日期 yyyy-MM
    public static List<String> getBeforeTenMonth(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF5.parse(time);
            Calendar cal = Calendar.getInstance();
            for (int i = 0; i < 12; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, -i);
                list.add(SDF5.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }



    /**
     * 获取指定时间往前推最近六个月日期 yyyyMM
     * @param
     */

    public static List<String> getBeforeSixYyyyMMdd(String time) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Date parse = SDF2.parse(time);
            Calendar cal = Calendar.getInstance();

            int day = new Random().nextInt(15) % (15 - 5 + 1) + 5;
            cal.setTime(parse);
//            cal.add(Calendar.MONTH, -7);
            cal.add(Calendar.DATE, day);
            list.add(SDF3.format(cal.getTime()));

            for (int i = 0; i < 5; i++) {
                cal.setTime(parse);
                cal.add(Calendar.MONTH, -i-1);
                list.add(SDF3.format(cal.getTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }


    public static String getAfter1Or2Month(String time) {
        try {
            Date parse = SDF4.parse(time);
            Calendar cal = Calendar.getInstance();

            int day = new Random().nextInt(60) % (60 - 30 + 1) + 30;
            cal.setTime(parse);
            cal.add(Calendar.DATE, -day);

            return SDF4.format(cal.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // yyyy-MM 往后推term个月
    public static String getEndMonth(String time, int term) {
        try {
            Date parse = SDF5.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parse);
            cal.add(Calendar.MONTH, term);
            return SDF5.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    // 昨天开始
    public static String getYesterdayBgnTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        return SDF3.format(cal.getTime()) + "000000";
    }

    // 昨天结束
    public static String getYesterdayEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 0);
        return SDF3.format(cal.getTime()) + "000000";
    }


    public static void main(String[] args) {

        String send_date = "2018-06-23 14:13:19";
        String year = send_date.substring(0,4);
        System.out.println(send_date);
        if (year.equals("2018"))
            send_date = DateUtils.getAfterMonth(send_date);


        System.out.println(send_date);

        System.out.println(getAfterMonth3("2018-10"));

        System.out.println(getYesterday());
        System.out.println(getYesterdayBgnTime());
        System.out.println(convTime2("2020-05-25 18:01:16"));


//        System.out.println(getYesterdayEndTime().substring(0, 8));
//        System.out.println(getEndMonth("2019-12", 10));
    }

}
