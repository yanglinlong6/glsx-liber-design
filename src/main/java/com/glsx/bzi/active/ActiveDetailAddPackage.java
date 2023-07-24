package com.glsx.bzi.active;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

/**
 * 激活详情添加套餐数据，
 * 只匹配type为8的GPS设备
 */

public class ActiveDetailAddPackage {
    public static void main(String[] args) throws IOException {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_biz_data"));
//        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        //宁夏福立升汽车销售服务有限公司|2018-10  --> 嘀加基础版|23
        TreeMap<String, String> packageMap = getPackageMapForNew();
        System.out.println("packageMap.size()=" + packageMap.size());

        // 宁夏福立升汽车销售服务有限公司|2018-10  --> SN1|SN2|...
        Map<String, String> activeDetailMap = getActiveDetailMap(mysqlConn);
        System.out.println("activeDetailMap.size()=" + activeDetailMap.size());

        // 已经选择过设备
        HashMap<String, Integer> savaMap = new HashMap<>();

        int sumTotal = 0;
        int sumNotFit = 0;
        for(Map.Entry<String, String> entry : packageMap.entrySet()) {
            String[] splitK = entry.getKey().split("\\|");
            String merchant = splitK[0];
            String statTime = splitK[1];

            String[] splitV = entry.getValue().split("\\|");
            String packName = splitV[0];
            int statCount = Integer.valueOf(splitV[1]);
            sumTotal += statCount;

            int sumCanset = 0;
            boolean isFit = false;
            ArrayList<String> candList = new ArrayList<>();
            List<String> afterMonth = DateUtils.getBeforeTenMonth(statTime);
            for (int i = 0; i < afterMonth.size(); i++) {
                String candSet = activeDetailMap.get(merchant + "|" + afterMonth.get(i));
                if (candSet == null) continue;

                String[] split = candSet.split("\\|");
                for (int index = 0; index < split.length; index++) {
                    if (savaMap.get(split[index]) != null) continue;

                    savaMap.put(split[index], 1);
                    candList.add(split[index] + "," + packName );
                    sumCanset++;

                    if(candList.size() == statCount) {
                        isFit = true;
                        break;
                    }
                }
                if (isFit) break;
            }

            if (sumCanset != statCount)
                System.out.println(entry.getKey() + " is not fit, statCount=" + statCount + ",sumCanset=" + sumCanset + ",diff=" + (statCount-sumCanset));

            sumNotFit += statCount-sumCanset;
            if (candList.size() == 0) continue;

            if (entry.getKey().equals("临沂悦新汽车销售服务有限公司|2017-04"))
                System.out.println(entry.getKey());
            runUpdate(mysqlConn, candList);
        }

        System.out.println("sumTotal=" + sumTotal + ", sumNotFit=" + sumNotFit);
    }

    // 除去滴加基础版
    private static TreeMap<String, String> getPackageMapForNew() throws IOException {
        TreeMap<String, String> packageMap = new TreeMap<String, String>();
        int sumPackage = 0;

        BufferedReader br = new BufferedReader(new FileReader(new File("data/active/baseData")));
        String lineTxt = null;
        while ((lineTxt=br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String key = split[2].trim() + "|" + split[0].trim() + "-" + getMonth(split[1].trim());
            if (split[3].trim().equals("D+基础版")) continue;

            packageMap.put(key, split[3].trim() + "|" + split[4].trim());
            sumPackage += Integer.valueOf(split[4].trim());
        }

        System.out.println("sumPackage=" + sumPackage);
        return packageMap;
    }

    private static String getMonth(String month) {
        month = month.replace("月", "");
        if (Integer.valueOf(month) < 10)
            return "0" + month;
        else
            return month;
    }

    private static void runUpdate(Connection mysqlConn, ArrayList<String> candList) {
        int size = candList.size();

        String tmpsql = "update dj_device_info set pkname = case device_sn \n";

        String tmpStr = "(";
        String values = "";
        for (int i = 0; i < size; i++) {
            String[] value = candList.get(i).split("\\,");
            tmpStr += "'" + value[0] + "',";
            values += "when '" + value[0] + "' then '" + value[1] + "'\n";
        }

        if (values.equals("")) return;
        tmpsql += values + "end where device_sn in " + tmpStr.substring(0, tmpStr.length()-1) + ")";

        MysqlConn.executeUpdate(mysqlConn, tmpsql);
    }

    private static Map<String,String> getActiveDetailMap(Connection mysqlConn) {
        HashMap<String, String> activeDetailMap = new HashMap<>();
//        String sql = "select device_sn,merchant,device_active_time from dj_device_info where device_active_time is not null and device_type!=66";
        String sql = "select device_sn,merchant,active_time from dj_device_info where device_active_time is not null and device_type!=66";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String key = rs.getString(2) + "|" + DateUtils.convTime1(rs.getString(3));
                    String value = rs.getString(1);
                    if (activeDetailMap.get(key) == null)
                        activeDetailMap.put(key, value);
                    else
                        activeDetailMap.put(key, activeDetailMap.get(key) + "|" + value);
                }
            }
        });

        return activeDetailMap;
    }

    private static TreeMap<String, String> getPackageMap() throws IOException {
        TreeMap<String, String> packageMap = new TreeMap<String, String>();
        int sumPackage = 0;

        // 2018年数据
        BufferedReader br = new BufferedReader(new FileReader(new File("data/active/baseData1")));
        String lineTxt = null;
        while ((lineTxt=br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String key = split[1].trim() + "|" + year2018(split[0].trim());
            packageMap.put(key, split[2].trim() + "|" + split[3].trim());
            sumPackage += Integer.valueOf(split[3].trim());
        }

        // 2019年数据
        br = new BufferedReader(new FileReader(new File("data/active/baseData2")));
        lineTxt = null;
        while ((lineTxt=br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String key = split[1].trim() + "|" + year2019(split[0].trim());
            packageMap.put(key, split[2].trim() + "|" + split[3].trim());
            sumPackage += Integer.valueOf(split[3].trim());
        }

        // 2020年数据
        br = new BufferedReader(new FileReader(new File("data/active/baseData3")));
        lineTxt = null;
        while ((lineTxt=br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String key = split[1].trim() + "|" + year2020(split[0].trim());
            packageMap.put(key, split[2].trim() + "|" + split[3].trim());
            sumPackage += Integer.valueOf(split[3].trim());
        }

        System.out.println("sumPackage=" + sumPackage);
        return packageMap;
    }

    public static String year2018(String month) {
        month = month.replace("月", "");
        if (Integer.valueOf(month) < 10)
            return "2018-0" + month;
        else
            return "2018-" + month;
    }

    public static String year2019(String month) {
        month = month.replace("月", "");
        if (Integer.valueOf(month) < 10)
            return "2019-0" + month;
        else
            return "2019-" + month;
    }

    public static String year2020(String month) {
        month = month.replace("月", "");
        if (Integer.valueOf(month) < 10)
            return "2020-0" + month;
        else
            return "2020-" + month;
    }

}
