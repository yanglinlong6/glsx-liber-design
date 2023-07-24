package com.glsx.bzi.test;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

public class MyTest {
    public static void main(String[] args) throws IOException {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));
        computeAccum(mysqlConn);
        MysqlConn.close(mysqlConn);
    }

    private static void computeAccum(Connection mysqlConn) throws IOException {
        HashMap<String, Integer> serverMap = new HashMap<>();

        TreeMap<String, String> activeMap = new TreeMap<>();
        TreeMap<String, Integer> activeMapByMon = new TreeMap<>();

        String sql = "SELECT settle_merchant,DATE_FORMAT(active_time,'%Y-%m'),COUNT(1)  FROM dj_device_settle_details where settle_merchant \n" +
                "in (SELECT DISTINCT settle_merchant FROM dj_device_settle_details where active_time is not null and settle_time is null) \n" +
                "GROUP BY settle_merchant,DATE_FORMAT(active_time,'%Y-%m') ORDER BY settle_merchant,DATE_FORMAT(active_time,'%Y-%m')";

        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String server = rs.getString(1);
                    String monTime = rs.getString(2);
                    int count = rs.getInt(3);

                    serverMap.put(server, 1);
                    activeMapByMon.put(server + "|" + monTime, count);


                    if (activeMap.get(server) == null)
                        activeMap.put(server, monTime+","+count);
                    else
                        activeMap.put(server, activeMap.get(server) + "|" + (monTime+","+count));
                }
            }
        });


        TreeMap<String, Integer> settleMap = new TreeMap<>();
        TreeMap<String, Integer> settleMapByMon = new TreeMap<>();
        sql = "SELECT settle_merchant,DATE_FORMAT(settle_time,'%Y-%m') as monthx,SUM(total) as total FROM dj_direct_settlement_monthly \n" +
                "where channel = '' and settle_merchant in (\n" +
                "SELECT DISTINCT settle_merchant FROM dj_device_settle_details where active_time is not null  and settle_time is null\n" +
                ") GROUP BY settle_merchant,DATE_FORMAT(settle_time,'%Y-%m') ORDER BY settle_merchant,DATE_FORMAT(settle_time,'%Y-%m')";

        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String server = rs.getString(1);
                    String monTime = rs.getString(2);
                    int count = rs.getInt(3);

                    serverMap.put(server, 1);
                    settleMapByMon.put(server + "|" + monTime, count);

                    settleMap.put(server + "|" + monTime, count);
                }
            }
        });


        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data/delivery/result")));
        StringBuffer buffer = new StringBuffer();

        List<String> heardMons = getMonTime();
        buffer.append("结算客户,");
        for (int i = 0; i < heardMons.size(); i++) {
            buffer.append(heardMons.get(i) + "累计激活").append(",");
            buffer.append(heardMons.get(i) + "累计结算").append(",");
            buffer.append(heardMons.get(i) + "累计差异").append(",");
        }
        buffer.append("\n");
        bw.write(buffer.toString());
        bw.flush();

        buffer.setLength(0);

        for (Map.Entry<String, Integer> entry : serverMap.entrySet()) {
            String server = entry.getKey();
            List<String> monTime = getMonTime();

            int sumTmpActive = 0;
            int sumTmpSettle = 0;

            buffer.setLength(0);
            buffer.append(server + ",");
            for (int i = 0; i < monTime.size(); i++) {
                String month = monTime.get(i);
                Integer activeValue = activeMapByMon.get(server + "|" + month);
                Integer settleValue = settleMapByMon.get(server + "|" + month);

                sumTmpActive += (activeValue==null ? 0 : activeValue);
                sumTmpSettle += (settleValue==null ? 0 : settleValue);

                buffer.append(sumTmpActive + "," + sumTmpSettle + "," + (sumTmpActive-sumTmpSettle) + ",");
            }

            buffer.append("\n");
            bw.write(buffer.toString());
            bw.flush();

            buffer.setLength(0);
        }
    }


    private static List<String> getMonTime() {
        List<String> monTime = new ArrayList<>();
//        for (int i = 1; i < 13; i++) {
//            String time = i < 10 ? "0" + i : String.valueOf(i);
//            monTime.add("2017-" + time);
//        }
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
        return  monTime;
    }
}
