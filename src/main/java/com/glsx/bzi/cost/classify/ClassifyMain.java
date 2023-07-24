package com.glsx.bzi.cost.classify;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.TreeMap;

public class ClassifyMain {
    public static void main(String[] args) throws IOException {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        BufferedReader br = new BufferedReader(new FileReader(new File("data/cost/classify/classify")));
        TreeMap<String, String> classifyMaps = new TreeMap<>();
        String lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            classifyMaps.put(split[0].trim(), split[1].trim());
        }

        for (Map.Entry<String, String> entry : classifyMaps.entrySet()) {
            String sql = "insert into t_settle_merchant_channel_new(settle_mer_name,channel) " +
                    "values('" + entry.getKey() + "','" + entry.getValue() + "')";
            MysqlConn.executeUpdate(mysqlConn, sql);
        }

        MysqlConn.close(mysqlConn);
    }
}
