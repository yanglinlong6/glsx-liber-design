package com.glsx.bzi.cost;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 分摊数据写入Excel表格
 */

public class WriteExcelForMoneyMain {
    public static void main(String[] args) throws IOException {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        TreeMap<String, Double> merMoneyMap = new TreeMap<>();
        String sql = "select settle_merchant,mon_time,price from dj_share_price";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String key = rs.getString(1) + "|" + rs.getString(2);
                    merMoneyMap.put(key, rs.getDouble(3));
                }
            }
        });

        ArrayList<String> serverList = new ArrayList<>();
        sql = "SELECT DISTINCT(settle_merchant) from dj_share_price";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    serverList.add(rs.getString(1));
                }
            }
        });


        List<String> monTime = getMonTime();
        System.out.print("服务商,");
        for (int i = 0; i < monTime.size(); i++) {
            System.out.print(monTime.get(i)+"分摊" + ",");
        }
        System.out.println();


        for (int i = 0; i < serverList.size(); i++) {
            String server = serverList.get(i);

            String monValues = "";
            for (int monIndex = 0; monIndex < monTime.size(); monIndex++) {
                String month = monTime.get(monIndex);
                String key = server + "|" + month;
                monValues += (merMoneyMap.get(key) == null ? 0.0 : merMoneyMap.get(key)) + ",";
            }

            System.out.println(server + "," + monValues);

        }

        MysqlConn.close(mysqlConn);
    }

    private static List<String> getMonTime() {
        List<String> monTime = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2017-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2018-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2019-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2020-" + time);
        }

        // 后续摊分都加上
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2021-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2022-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2023-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2024-" + time);
        }
        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2025-" + time);
        }

        for (int i = 1; i < 13; i++) {
            String time = i < 10 ? "0" + i : String.valueOf(i);
            monTime.add("2026-" + time);
        }

        return  monTime;
    }
}
