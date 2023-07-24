package com.glsx.util;

public class SystemUtils {
    public static boolean isWindows() {
        return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;
    }

    public static void main(String[] args) {

//        System.out.println(System.getProperties().getProperty("os.name").toLowerCase());
        System.out.println(isWindows());
    }
}
