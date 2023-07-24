package com.glsx.util;

public class RowkeyUtils {

    // 替换时间
    public static String convert3Col(String rowkey, int year) {
        String time = rowkey.split("\\-")[2];
        String convTime = DateOldUtils.convertTime1(time, year);
        return rowkey.replace(time, convTime);
    }

    // 替换SN
    public static String convert2Col(String rowkey, String convSn) {
        String sn = rowkey.split("\\-")[1];
        String tmpStr = "";
        for (int i = 0; i < 14-convSn.length(); i++) {
            tmpStr += "0";
        }

        return rowkey.replace(sn, tmpStr+convSn);
    }

    // 替换时间和SN
    public static String convert(String rowkey, String convSn, int year) {
        String[] split = rowkey.split("\\-");
        String sn = split[1];
        String time = split[2];
        String convTime = DateOldUtils.convertTime1(time, year);

        String tmpStr = "";
        for (int i = 0; i < 14-convSn.length(); i++) {
            tmpStr += "0";
        }
       return rowkey.replace(sn, tmpStr + convSn).replace(time, convTime);
    }

    // 替换时间和SN
    public static String convert(String rowkey, String convSn, int year, int mon) {
        String[] split = rowkey.split("\\-");
        String sn = split[1];
        String time = split[2];
        String convTime = DateOldUtils.convertTime1(time, year, mon);

        String tmpStr = "";
        for (int i = 0; i < 14-convSn.length(); i++) {
            tmpStr += "0";
        }
        return rowkey.replace(sn, tmpStr + convSn).replace(time, convTime);
    }



    public static void main(String[] args) {
        System.out.println(convert3Col("05-00007151111762-20151126133725", 5));
        System.out.println(convert("05-00007151111762-20151126133725", "8151111762",5));
    }
}
