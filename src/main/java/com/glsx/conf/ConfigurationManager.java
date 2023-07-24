package com.glsx.conf;

import com.glsx.constant.Constants;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static Properties prop = new Properties();
    static {
        try {
            InputStream in = ConfigurationManager.class.getClassLoader().getResourceAsStream("conf/my.properties");
            prop.load(in);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static Integer getInteger(String key) {
        return Integer.valueOf(prop.getProperty(key));
    }

    public static Boolean getBoolean(String key) {
        return Boolean.valueOf(prop.getProperty(key));
    }

    public static Long getLong(String key) {
        return Long.valueOf(prop.getProperty(key));
    }


    public static void main(String[] args) {
        System.out.println(ConfigurationManager.getProperty("ali_quorum_pub"));
    }
}
