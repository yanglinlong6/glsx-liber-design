package com.glsx.bzi.cost.income;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

public class ActiveMerchantShare {

    public static void main(String[] args) {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        // 直销配激活  代销(含广汇)配结算
        String channel = "('直销')";
        //String channel = "('代销','广汇')";

        //String type2 = "settle_time";
        String type2 = "active_time";

        computeShareMoney(mysqlConn,channel, type2);
        MysqlConn.close(mysqlConn);
    }

    private static void computeShareMoney(Connection mysqlConn, String channel, String type2) {
        // 客户|(结算/激活)时间 -> 结算金额

        TreeMap<String, Double> shareMoneyMap = new TreeMap<>();
        String sql = "SELECT " + type2 + ",settle_merchant,active_merchant,pk_period,pk_price,hw_price,install_price from dj_2021_settle_details_agg " +
                    "where device_type=8 and channel in " + channel +" and " + type2 + " is not null " + " and " + type2 + " < '2021-01-01'";


        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String time = rs.getString(1);
                    String settle_merchant = rs.getString(2).trim();
                    String active_merchant = rs.getString(3).trim();
                    int pk_period = rs.getInt(4);
                    double pkPrice = rs.getDouble(5);
                    double hwPrice = rs.getDouble(6);
                    double installPrice = rs.getDouble(7);

                    double price = pkPrice + hwPrice +installPrice;

                    //if (classifyMap.get(settle_merchant) == null) continue;

                    String monTime = "";
                    /*if (settle_merchant.equals("广汇汽车") && type2.equals("settle_time"))
                        monTime = DateUtils.getAfterMonth2(time);
                    else*/
                    monTime = DateUtils.convTime1(time);

                    double priceMonth = price/pk_period;
                    String key = "";
                    for (int i = 0; i < pk_period; i++) {
                        key = settle_merchant + "|" + monTime;
                        if (shareMoneyMap.get(key) == null)
                            shareMoneyMap.put(key, priceMonth);
                        else
                            shareMoneyMap.put(key, shareMoneyMap.get(key) + priceMonth);

                        monTime = DateUtils.getAfterMonth(monTime);

                    }
                }
            }
        });

        MysqlConn.executeUpdate(mysqlConn, "TRUNCATE TABLE dj_share_price");

        String tmpsql = "INSERT INTO dj_share_price(settle_merchant,mon_time,price) values";
        String values = "";

        for (Map.Entry<String, Double> entry : shareMoneyMap.entrySet()) {
            String[] split = entry.getKey().split("\\|");
            String settle_merchant = split[0];
            String mon_time = split[1];
            Double price = entry.getValue();
            values += "('" + settle_merchant + "','" + mon_time + "'," + price + "),";

            tmpsql += values.substring(0, values.length() - 1);
            MysqlConn.executeUpdate(mysqlConn, tmpsql);

            tmpsql = "INSERT INTO dj_share_price(settle_merchant,mon_time,price) values";
            values = "";
        }

    }
}
