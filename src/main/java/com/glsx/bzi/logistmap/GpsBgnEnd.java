package com.glsx.bzi.logistmap;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlMutiConn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备GPS上点的对应的起止时间，本程序定期调度一次
 */

public class GpsBgnEnd {
    public static void main(String[] args) {
        MysqlMutiConn.addConnection("pap_dev", ConfigurationManager.getProperty("mysql_pap"));
        MysqlMutiConn.addConnection("device_mapping", ConfigurationManager.getProperty("mysql_prd_login"));

        Connection pap_dev = MysqlMutiConn.getConnection("pap_dev");
        Connection device_mapping = MysqlMutiConn.getConnection("device_mapping");

        Map<String, String> papMap = getPap(pap_dev);
        Map<String, String> deviceMap = getDevice(device_mapping);

        String tmpSql = "replace into t_pap_end_info(sn,bgnTime,endTime) values";
        String values = "";
        int index = 0;
        int sumNotFit = 0;
        for(Map.Entry<String, String> entry : deviceMap.entrySet()) {
            String fakeSn = papMap.get(entry.getKey());
            if (fakeSn == null) {
                sumNotFit++;
                continue;
            }
            String value = entry.getValue();
            values += "('" + fakeSn + ",'" + value + "'),";
            if (++index %1000 == 0) {
                tmpSql += values.substring(0, values.length()-1);
//                MysqlMutiConn.executeUpdate(pap_dev, tmpSql);
                tmpSql = "replace into t_pap_end_info(sn,bgnTime,endTime) values";
                values = "";
            }
        }

        System.out.println("total=" + index + ",sumNotFit=" + sumNotFit);
    }

    private static Map<String, String> getDevice(Connection device_mapping) {
        Map<String, String> deviceMap = new HashMap<String, String>();
        MysqlMutiConn.executeQuery(device_mapping, "select realSn,fakeSn from device_mapping", new MysqlMutiConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    deviceMap.put(rs.getString(1), rs.getString(2));
                }
            }
        });
        return deviceMap;
    }

    private static Map<String, String> getPap(Connection pap_dev) {
        Map<String, String> papMap = new HashMap<String, String>();
        MysqlMutiConn.executeQuery(pap_dev, "select sn,firstGpsTime,todayGpsTime from t_pap_dev", new MysqlMutiConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    papMap.put(rs.getString(1), rs.getString(2) + "," + rs.getString(3));
                }
            }
        });
        return papMap;
    }
}
