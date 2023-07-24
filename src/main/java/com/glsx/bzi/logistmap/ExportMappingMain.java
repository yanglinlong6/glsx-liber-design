package com.glsx.bzi.logistmap;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.connection.MysqlMutiConn;
import com.glsx.util.DateUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据区域去映射SN
 */

public class ExportMappingMain {
    public static void main(String[] args) throws IOException {
        //writeFile();
        //if (1==1) return;

        long bgnTime = System.currentTimeMillis();
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data/logistmap/result")));
        StringBuffer buffer = new StringBuffer();

        // 存储已经存在的设备
        HashMap<String, Integer> savaMap = new HashMap<>();

        // 获取D+设备SN
        HashMap<String, Integer> snsMapForD = getSnsMapForD();

        // <2020-09-07|西宁 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁|5,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁>
        HashMap<String, String> dtCityMap = readFile();
        System.out.println("dtCityMap.size()=" + dtCityMap.size());

        // <2020-09-07|西宁 = 1790225300-8|1700112234-8>
        HashMap<String, String> dtCitySnsMap = getSnsMap();
        System.out.println("dtCitySnsMap.size()=" + dtCitySnsMap.size());

        for(Map.Entry<String, String> entry : dtCityMap.entrySet()){
            // <2020-09-07|西宁 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁|5,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁>
            String key1 = entry.getKey();
            String value1 = entry.getValue();

            String[] values1 = value1.split("\\|");
            int len1 = values1.length;

            int sumIndex = 0;
            // 获取最近10天的key
            List<String> nearDayKey = getNearDayKey(key1);
            for (int i = 0; i < nearDayKey.size(); i++) {
                String  value2 = dtCitySnsMap.get(nearDayKey.get(i));
                if (value2 != null) {
                    String[] values2 = value2.split("\\|");
                    int len2 = values2.length;
                    int minLen = len1 < len2 ? len1 : len2;
                    for (int index = 0; index < minLen; index++) {
                        String splitChild[] = values1[index+sumIndex].split("\\,");

                        String realsn = values2[index].split("\\-")[0];
                        String fakesn = splitChild[1];
                        if (snsMapForD.get(realsn) == null) continue;
                        savaMap.put(fakesn, 1);

                        buffer.append(splitChild[1] + "," +
                                values2[index].split("\\-")[0] + "," +
                                values2[index].split("\\-")[1] + "," +
                                splitChild[0] + "," +
                                splitChild[2] + "," +
                                splitChild[3] + "," +
                                splitChild[4] + "," +
                                splitChild[5] + "," +
                                splitChild[6]).append("\n");
                    }

                    bw.write(buffer.toString());
                    bw.flush();
                    buffer.setLength(0);

                    if (len1 <= len2) break;
                    else {
                        len1 = len1 - len2;
                        sumIndex += minLen;
                    }
                }
            }
            bw.write(buffer.toString());
            bw.flush();
            buffer.setLength(0);
        }


        // 跑第二遍 跑非D+设备
        for(Map.Entry<String, String> entry : dtCityMap.entrySet()){
            // <2020-09-07|西宁 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁|5,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁>
            String key1 = entry.getKey();
            String value1 = entry.getValue();

            String[] values1 = value1.split("\\|");
            int len1 = values1.length;

            int sumIndex = 0;
            // 获取最近10天的key
            List<String> nearDayKey = getNearDayKey(key1);
            for (int i = 0; i < nearDayKey.size(); i++) {
                String  value2 = dtCitySnsMap.get(nearDayKey.get(i));
                if (value2 != null) {
                    String[] values2 = value2.split("\\|");
                    int len2 = values2.length;
                    int minLen = len1 < len2 ? len1 : len2;
                    for (int index = 0; index < minLen; index++) {
                        String splitChild[] = values1[index+sumIndex].split("\\,");

                        String fakesn = splitChild[1];
                        if (savaMap.get(fakesn) != null) continue;
                        savaMap.put(fakesn, 1);

                        buffer.append(splitChild[1] + "," +
                                values2[index].split("\\-")[0] + "," +
                                values2[index].split("\\-")[1] + "," +
                                splitChild[0] + "," +
                                splitChild[2] + "," +
                                splitChild[3] + "," +
                                splitChild[4] + "," +
                                splitChild[5] + "," +
                                splitChild[6]).append("\n");
                    }

                    bw.write(buffer.toString());
                    bw.flush();
                    buffer.setLength(0);

                    if (len1 <= len2) break;
                    else {
                        len1 = len1 - len2;
                        sumIndex += minLen;
                    }
                }
            }
            bw.write(buffer.toString());
            bw.flush();
            buffer.setLength(0);
        }



        bw.write(buffer.toString());
        bw.flush();
        bw.close();

        System.out.println("The job total cost " + (System.currentTimeMillis()-bgnTime)/1000 + "s");
    }

    private static HashMap<String, Integer> getSnsMapForD() {
        HashMap<String, Integer> SnsMapForD = new HashMap<>();
        MysqlMutiConn.addConnection("FORD", ConfigurationManager.getProperty("mysql_prd_login"));
        Connection ford = MysqlMutiConn.getConnection("FORD");
        String sql = "select device_sn from dj_devices";
        MysqlMutiConn.executeQuery(ford, sql, new MysqlMutiConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    SnsMapForD.put(rs.getString(1), 1);
                }
            }
        });
        MysqlMutiConn.close(ford);

        return SnsMapForD;
    }

    // 从2020-09-07|西宁 得到从2020-09-07|西宁市,2020-09-08|西宁市,...,2020-09-17|西宁市
    private static List<String> getNearDayKey(String key1) {
        ArrayList<String> list = new ArrayList<>();
        String[] split = key1.split("\\|");
        String key = split[0];
        String value = split[1] + "市";
        List<String> beforeTenDay = DateUtils.getBeforeTenDay(key);
        for (int i = 0; i < beforeTenDay.size(); i ++) {
            list.add(beforeTenDay.get(i) + "|" + value);
        }

        return list;
    }

    //  <2020-09-07|西宁 = 1790225300-4|1700112234-5>
    private static HashMap<String, String> getSnsMap() {
        HashMap<String, String> dtCitySnsMap = new HashMap<>();
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_pap"));
        String sql = "select userId,date_format(activeDate, '%Y-%m-%d'),activeCity,devType from t_pap_first_active_pos_info where activeDate > '2021-12-01'";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String key = rs.getString(2) + "|" + rs.getString(3);
                    if (dtCitySnsMap.get(key) == null)
                        dtCitySnsMap.put(key, rs.getString(1)+"-"+String.valueOf(rs.getInt(4)));
                    else {
                        if (dtCitySnsMap.get(key).contains(rs.getString(1))) continue;
                        dtCitySnsMap.put(key, dtCitySnsMap.get(key) + "|" + rs.getString(1) + "-" + String.valueOf(rs.getInt(4)));
                    }
                }
            }
        });

        MysqlConn.close(mysqlConn);
        return dtCitySnsMap;
    }

    //  <2020-09-07|西宁 = 1790225300-4|1700112234-5>
    private static HashMap<String, String> getSnsMapForD111() {
        HashMap<String, String> dtCitySnsMap = new HashMap<>();
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_pap"));
        String sql = "select userId,date_format(activeDate, '%Y-%m-%d'),activeCity,devType from t_pap_first_active_pos_info";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String key = rs.getString(2) + "|" + rs.getString(3);
                    if (dtCitySnsMap.get(key) == null)
                        dtCitySnsMap.put(key, rs.getString(1)+"-"+String.valueOf(rs.getInt(4)));
                    else {
                        if (dtCitySnsMap.get(key).contains(rs.getString(1))) continue;
                        dtCitySnsMap.put(key, dtCitySnsMap.get(key) + "|" + rs.getString(1) + "-" + String.valueOf(rs.getInt(4)));
                    }
                }
            }
        });

        MysqlConn.close(mysqlConn);
        return dtCitySnsMap;
    }

    private static HashMap<String, String> readFile() throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(new File("data/logistmap/mapping")));
        BufferedReader br = new BufferedReader(read);
        String lineTxt = null;
        //<2020-09-07|西宁 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁,44192128,甘肃赛亚汽车销售服务有限公司>

        HashMap<String, String> dtCityMap = new HashMap<>();
        while((lineTxt = br.readLine()) != null) {
            String[] split = lineTxt.split("\\,");
            if (split[4].equals("")) continue;
//            System.out.println(lineTxt);
            String dt = split[2].substring(0, 10);
            String key = dt + "|" + split[4];

            if (dtCityMap.get(key) == null)
                dtCityMap.put(key, lineTxt);
            else
                dtCityMap.put(key, dtCityMap.get(key) + "|" + lineTxt);

        }
        return dtCityMap;
    }

    private static void writeFile() throws IOException {
//        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_dev_login"));
//        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));

        BufferedWriter bw = new BufferedWriter(new FileWriter("data/logistmap/mapping"));
        StringBuffer buffer = new StringBuffer();

        // dj_device_info/external_device_info
//        String sql = "select a.device_type,a.device_sn,a.active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name \n" +
//                "from dj_device_info_gps a \n" +
//                "join dj_system_merchant b on a.merchant=b.merchant_name " +
//                "where city_name is not null and active_time<'2021-01-01:00:00:00' " +
//                "and device_type != 66";

//        String sql = "select a.device_type,a.device_sn,a.active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name \n" +
//                "from dj_device_info_gps a \n" +
//                "left join dj_system_merchant b on a.merchant=b.merchant_name ";

//        String sql = "select a.device_type,a.sn,a.active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name \n" +
//                "from dj_2021_settle_details_gps a \n" +
//                "left join dj_system_merchant b on a.active_merchant=b.merchant_name where a.active_time is not null";

        String sql = "select a.device_type,a.sn,a.active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name " +
                "from dj_2021_settle_details a left join dj_system_merchant b on a.active_merchant=b.merchant_name " +
                "where a.active_time >= '2022-01-01' and device_type = 8";

        String newSql = "";


//        String sql1 = "select a.device_type,a.device_sn,a.device_active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name \n" +
//                "from external_device_info a \n" +
//                "join dj_system_merchant b on a.merchant_id=b.merchant_id where city_name is not null and device_type != 66";


//        String sql = "select a.device_type,a.device_sn,a.device_active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name\n" +
//                "from dj_device_info a \n" +
//                "join dj_system_merchant b on a.merchant_id=b.merchant_id\n" +
//                "where city_name is not null and device_active_time<'2021-01-01:00:00:00' and device_type != 66\n" +
//                "UNION\n" +
//                "select a.device_type,a.device_sn,a.active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name\n" +
//                "from external_device_info a\n" +
//                "join dj_system_merchant b on a.merchant_id=b.merchant_id where city_name is not null and device_type != 66";

        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
//        MysqlConn.executeFlowQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                int index = 0;
                while (rs.next()) {
                    buffer.append(rs.getInt(1) + "," +
                            rs.getString(2) + "," +
                            rs.getString(3) + "," +
                            rs.getString(4) + "," +
                            rs.getString(5) + "," +
                            rs.getInt(6) + "," +
                            rs.getString(7)).append("\n");

                    if (++index % 1000 == 0) {
                        bw.write(buffer.toString());
                        bw.flush();
                        buffer.setLength(0);
                    }
                }
                bw.write(buffer.toString());
                bw.flush();
                bw.close();
                buffer.setLength(0);

                System.out.println("总共导出数据条数为:" + index);
            }
        });

        MysqlConn.close(mysqlConn);
    }
}
