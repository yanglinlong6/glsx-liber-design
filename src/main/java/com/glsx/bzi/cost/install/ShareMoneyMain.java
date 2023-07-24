package com.glsx.bzi.cost.install;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * 硬件成本分摊算法  分渠道/直销 和 结算/激活维度
 */
public class ShareMoneyMain {
    public static void main(String[] args) {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        // 直销配激活  代销(含广汇)配结算

        //String type1 = "('直销')";
        String type1 = "('代销','广汇')";
        //String type1 = "广汇";

        String type2 = "settle_time";
        //String type2 = "active_time";
        int isReturn = 0;//退货

        computeShareMoney(mysqlConn, type1, type2, isReturn);

        MysqlConn.close(mysqlConn);
    }

    private static void computeShareMoney(Connection mysqlConn, String type1, String type2, int isReturn) {
        // 客户|(结算/激活)时间 -> 结算金额
        TreeMap<String, Double> shareMoneyMap = new TreeMap<>();
        /*
        String sql = "SELECT sn," + type2 + ",settle_merchant,period,price from t_install_cost " +
                "where " + type2 + " not in ('null', 'None') and " + type2 + " is not null and  mer_type='" + type1 + "'" ;//+ " and " + type2 + "<'2021-01-01'";
         */
        String sql = "";
        if (isReturn == 0){
            sql = "SELECT sn," + type2 + ",settle_merchant,pk_period,install_cost from dj_2021_settle_details_agg " +
                    "where " + type2 + " !='null'  and install_cost is not null and  channel in " + type1 ;//+ " and " + type2 + "<'2021-01-01'";
        }
        else {
            sql = "SELECT sn," + type2 + ",settle_merchant,pk_period,install_cost from dj_2021_settle_details_return_agg " +
                    "where " + type2 + " !='null'  and install_cost is not null and  channel in " + type1 ;//+ " and " + type2 + "<'2021-01-01'";
        }


        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String sn = rs.getString(1).trim();
                    String time = rs.getString(2);
                    String settle_merchant = rs.getString(3).trim();
                    int pk_period = rs.getInt(4);
                    double price = rs.getDouble(5);

                    String monTime = "";

                    /*if (settle_merchant.equals("广汇汽车") && type2.equals("settle_time") &&
                            time.compareTo("2021-01-01") < 0 )
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

                        // 摊分截止
//                        if (monTime.equals("2021-01"))
//                            break;
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
