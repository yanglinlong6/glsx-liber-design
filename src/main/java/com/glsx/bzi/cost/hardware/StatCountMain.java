package com.glsx.bzi.cost.hardware;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * 结算、激活数量统计，成本
 */
public class StatCountMain {
    public static void main(String[] args) {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        TreeMap<String, Integer> settleCountMap = new TreeMap<>();
        TreeMap<String, Integer> activeCountMap = new TreeMap<>();

        TreeMap<String, Double> settlePriceMap = new TreeMap<>();
        TreeMap<String, Double> activePriceMap = new TreeMap<>();

//        String type ="渠道";
//        String type ="广汇";
        String type ="直销";
        String sql = "SELECT settle_merchant,DATE_FORMAT(settle_time,'%Y-%m'),DATE_FORMAT(active_time,'%Y-%m'),price FROM t_hardware_cost " +
                "where mer_type='" + type + "'";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String settle_merchant = rs.getString(1);
                    String settle_time = rs.getString(2);
                    String active_time = rs.getString(3);
                    double price = rs.getDouble(4);

                    String key = settle_merchant + "|" + settle_time;
                    // 个数
                    if (settleCountMap.get(key) == null)
                        settleCountMap.put(key, 1);
                    else
                        settleCountMap.put(key, settleCountMap.get(key)+1);

                    // 成本
                    if (settlePriceMap.get(key) == null)
                        settlePriceMap.put(key, price);
                    else
                        settlePriceMap.put(key, settlePriceMap.get(key)+price);

                    key = settle_merchant + "|" + active_time;
                    if (active_time == null) continue;
                    if (activeCountMap.get(key) == null)
                        activeCountMap.put(key, 1);
                    else
                        activeCountMap.put(key, activeCountMap.get(key)+1);
                }
            }
        });

        // 统计 结算中有
        for (Map.Entry<String, Integer> entry : settleCountMap.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue() + "|" +
                    (activeCountMap.get(entry.getKey())==null ? 0 : activeCountMap.get(entry.getKey())) + "|" +
                    settlePriceMap.get(entry.getKey()));
        }

        // 统计 激活中有，结算中无
        for (Map.Entry<String, Integer> entry : activeCountMap.entrySet()) {
            if (settleCountMap.get(entry.getKey())==null) {
                System.out.println(entry.getKey() + "|0|"
                        +entry.getValue() + "|");
            }
        }



        MysqlConn.close(mysqlConn);
    }
}
