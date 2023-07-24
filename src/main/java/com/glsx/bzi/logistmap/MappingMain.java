package com.glsx.bzi.logistmap;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.SystemUtils;
import org.apache.commons.collections.map.HashedMap;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据区域去映射SN
 */

public class MappingMain {
    public static void main(String[] args) throws IOException {
//        writeFile();
//        if (1==1) return;

        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_pap"));
        // <2020-09-07|西宁,6,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁|5,SH53A2007294775,2020-09-07 15:51:33.0,青海,西宁>

        HashMap<String, String> dtCityMap = readFile();
        System.out.println("dtCityMap.size()=" + dtCityMap.size());

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data/logistmap/result")));
        StringBuffer buffer = new StringBuffer();

        int count = 0;
        for(Map.Entry<String, String> entry : dtCityMap.entrySet()){
            String[] split = entry.getKey().split("\\|");
            String dt = split[0];
            String city = split[1] + "市";

            String[] values = entry.getValue().split("\\|");
            int len = values.length;

            String sql = "select userId,devType,activeDate,activeProvince,activeCity from t_pap_first_active_pos_info " +
                    "where date_format(activeDate, '%Y-%m-%d')='" + dt + "' and activeCity='" + city + "' limit " + values.length;

            if (++count % 10 == 0) System.out.println(sql);

            MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
                @Override
                public void process(ResultSet rs) throws Exception {
                    int index = 0;
                    while (rs.next()) {
                        // 6,SH53A2010228365,2020-10-31 15:34:36.0,吉林,长春,44202896,长春永汇汽车销售服务有限公司
                        String split[] = values[index].split("\\,");
                        buffer.append(split[1] + "," +
                                rs.getString(1) + "," +
                                split[0] + "," +
                                split[2] + "," +
                                split[3] + "," +
                                split[4] + "," +
                                split[5] + "," +
                                split[6]).append("\n");

                        index++;
                    }
                    bw.write(buffer.toString());
                    bw.flush();
                    buffer.setLength(0);
                }
            });
        }

        bw.close();
        MysqlConn.close(mysqlConn);
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
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_dev_login"));

        BufferedWriter bw = new BufferedWriter(new FileWriter("data/logistmap/mapping"));
        StringBuffer buffer = new StringBuffer();

        String sql1 = "select a.device_type,a.device_sn,a.active_time,b.province_name,b.city_name,b.merchant_id,b.merchant_name \n" +
                "from dj_device_info a \n" +
                "join dj_system_merchant b on a.merchant_id=b.merchant_id where city_name is not null";
        MysqlConn.executeQuery(mysqlConn, sql1, new MysqlConn.QueryCallback() {
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
