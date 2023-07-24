package com.glsx.bzi.logistmap;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据区域去映射SN  根据省匹配
 */

public class ExportMappingByProvMain {
    public static void main(String[] args) throws IOException {
        long bgnTime = System.currentTimeMillis();

        // <2020-09-07|青海 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁|5,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁>
        HashMap<String, String> dtCityMap = readFile();
        System.out.println("dtCityMap.size()=" + dtCityMap.size());

        // <2020-09-07|青海 = 1790225300|1700112234>
        HashMap<String, String> dtCitySnsMap = getSnsMap();
        System.out.println("dtCitySnsMap.size()=" + dtCitySnsMap.size());

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data/logistmap/result")));
        StringBuffer buffer = new StringBuffer();

        for(Map.Entry<String, String> entry : dtCityMap.entrySet()){
            // <2020-09-07|青海 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁|5,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁>
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

//                if (i > 9)
//                    System.out.println(key1 + " 完成了" + i + "轮");
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

    // 从2020-09-07|西宁 得到从2020-09-07|西宁市,2020-09-08|西宁市,...,2020-09-17|西宁市
    private static List<String> getNearDayKey(String key1) {
        ArrayList<String> list = new ArrayList<>();
        String[] split = key1.split("\\|");
        String key = split[0];
        String value = split[1];
        if (value.equals("新疆"))
            value = "新疆维吾尔自治区";
        else if (value.equals("内蒙古"))
            value = "内蒙古自治区";
        else if (value.equals("宁夏"))
            value = "宁夏回族自治区";
        else if (value.equals("广西"))
            value = "广西壮族自治区";
        else value = value + "省";

        List<String> beforeTenDay = DateUtils.getBeforeTenDay(key);
        for (int i = 0; i < beforeTenDay.size(); i ++) {
            list.add(beforeTenDay.get(i) + "|" + value);
        }

        return list;
    }

    //  <2020-09-07|青海 = 1790225300|1700112234>
    private static HashMap<String, String> getSnsMap() {
        HashMap<String, String> dtCitySnsMap = new HashMap<>();
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_pap"));
        String sql = "select userId,date_format(activeDate, '%Y-%m-%d'),activeProvince,devType from t_pap_first_active_pos_info";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String key = rs.getString(2) + "|" + rs.getString(3);
                    if (dtCitySnsMap.get(key) == null)
                        dtCitySnsMap.put(key, rs.getString(1)+"-"+String.valueOf(rs.getInt(4)));
                    else
                        dtCitySnsMap.put(key, dtCitySnsMap.get(key) + "|" + rs.getString(1)+"-"+String.valueOf(rs.getInt(4)));
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
        //<2020-09-07|青海 = 6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁,44192128,甘肃赛亚汽车销售服务有限公司>

        HashMap<String, String> dtCityMap = new HashMap<>();
        while((lineTxt = br.readLine()) != null) {
            String[] split = lineTxt.split("\\,");
            if (split[3].equals("")) continue;
            String dt = split[2].substring(0, 10);
            String key = dt + "|" + split[3];

            if (dtCityMap.get(key) == null)
                dtCityMap.put(key, lineTxt);
            else
                dtCityMap.put(key, dtCityMap.get(key) + "|" + lineTxt);

        }
        return dtCityMap;
    }
}
