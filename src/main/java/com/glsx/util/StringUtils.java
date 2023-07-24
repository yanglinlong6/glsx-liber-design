package com.glsx.util;

import java.util.HashMap;

public class StringUtils {
    public StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str) || " ".equals(str);
    }

    public static String[] checkData(String data) {
        String[] tmp = data.split(",");
        return tmp.length >= 3 && !isEmpty(tmp[0]) && !isEmpty(tmp[1]) && !isEmpty(tmp[2]) ? tmp : null;
    }

    public static void main(String[] args) {
        String time = "2020110301011";

        HashMap<String, HashMap<String, String>> testMaps = new HashMap<>();





    }
}
