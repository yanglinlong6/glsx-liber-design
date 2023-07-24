package com.glsx.bzi.cost.barcode;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

/**
 * 广汇条形码安装分摊
 */

public class ShareMoneyMain {
    public static void main(String[] args) throws IOException {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        // 客户|结算时间 -> 结算金额
        TreeMap<String, Double> shareMoneyMap = new TreeMap<>();
//        BufferedReader br = new BufferedReader(new FileReader(new File("data/cost/barcode/income")));
        BufferedReader br = new BufferedReader(new FileReader(new File("data/cost/barcode/install")));
        String lineTxt = null;
        while ((lineTxt=br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String settle_merchant = split[0].trim();
            int pk_period = Integer.valueOf(split[1].trim());
            String monTime = split[2].trim();
            double price = Double.valueOf(split[3].trim());

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

        MysqlConn.close(mysqlConn);
    }
}
