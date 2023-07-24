package com.glsx.bzi.cost.income;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *  设备收入分摊
 */
public class ShareMoneyMain {
    public static void main(String[] args) {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        // 直销配激活  代销(含广汇)配结算
        //String channel = "('直销')";
        //String channel = "('代销','广汇')";

        //渠道 （原直销）
        //String channelEx = "((settle_time >= '2022-09-01' and channel = '渠道') or (settle_time < '2022-09-01' and channel = '直销'))";
        //String type2 = "active_time";

        //直销 （原代销）
        String channelEx = "((settle_time >= '2022-09-01' and channel in ('直销','广汇')) or" +
                " (settle_time < '2022-09-01' and channel in ('代销','广汇')))";
        String type2 = "settle_time";

        int isReturn = 1; //是否退货

        computeShareMoney(mysqlConn,channelEx, type2,isReturn);
        MysqlConn.close(mysqlConn);
    }

    private static HashMap<String, String> classifyMerchant(Connection mysqlConn, String type) {
        HashMap<String, String> classifyMap = new HashMap<>();
        String sql = "SELECT settle_mer_name,channel FROM t_settle_merchant_channel_new where channel='" + type + "'";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    classifyMap.put(rs.getString(1).trim(), rs.getString(2).trim());
                }
            }
        });

//        if (type.equals("直销"))
        if (type.equals("广汇"))
            classifyMap.put("广汇汽车", "广汇");

        return classifyMap;
    }


    private static void computeShareMoney(Connection mysqlConn, String channel, String type2, int isReturn ) {
        // 客户|(结算/激活)时间 -> 结算金额

        // 结算客户分类
        //HashMap<String, String> classifyMap = classifyMerchant(mysqlConn, channel);

        TreeMap<String, Double> shareMoneyMap = new TreeMap<>();
        String sql = "";

        /*
        if(isReturn == 0){
            sql = "SELECT " + type2 + ",settle_merchant,pk_period,pk_price,hw_price,install_price from dj_2021_settle_details " +
                    "where device_type=8 and channel='" + channel +"' and " + type2 + " is not null ";// + " and " + type2 + " < '2021-01-01'";
        }
        else {
            sql = "SELECT " + type2 + ",settle_merchant,pk_period,pk_price,hw_price,install_price from dj_2021_settle_details_return " +
                    "where channel='" + channel +"' and " + type2 + " is not null ";// + " and " + type2 + " < '2021-01-01'";
        }*/

        if(isReturn == 0){
            sql = "SELECT " + type2 + ",settle_merchant,pk_period,pk_price,hw_price,install_price from dj_2021_settle_details " +
                    "where device_type=8 and " + channel +" and " + type2 + " is not null ";
        }
        else {
            sql = "SELECT " + type2 + ",settle_merchant,pk_period,pk_price,hw_price,install_price from dj_2021_settle_details_return " +
                    "where device_type=8 and " + type2 + " is not null ";
        }

        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String time = rs.getString(1);
                    String settle_merchant = rs.getString(2).trim();
                    int pk_period = rs.getInt(3);
                    double pkPrice = rs.getDouble(4);
                    double hwPrice = rs.getDouble(5);
                    double installPrice = rs.getDouble(6);

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

        Integer  total = 0;

        for (Map.Entry<String, Double> entry : shareMoneyMap.entrySet()) {
            String[] split = entry.getKey().split("\\|");
            String settle_merchant = split[0];
            String mon_time = split[1];
            Double price = entry.getValue();
            values += "('" + settle_merchant + "','" + mon_time + "'," + price + "),";

            total += 1;
            if (total % 1000 == 0){

                tmpsql += values.substring(0, values.length() - 1);
                MysqlConn.executeUpdate(mysqlConn, tmpsql);

                tmpsql = "INSERT INTO dj_share_price(settle_merchant,mon_time,price) values";
                values = "";
                System.out.println("total = "+ total);
            }
        }

        if (total % 1000 != 0){
            tmpsql += values.substring(0, values.length() - 1);
            MysqlConn.executeUpdate(mysqlConn, tmpsql);
        }
        System.out.println("done total = "+ total);

    }
}
