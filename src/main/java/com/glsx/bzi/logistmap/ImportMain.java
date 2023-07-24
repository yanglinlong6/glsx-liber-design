package com.glsx.bzi.logistmap;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;

import java.io.*;
import java.sql.Connection;

/**
 * 将数据从文件中导入mysql
 */

public class ImportMain {
    public static void main(String[] args) throws IOException {
        long bgnTime = System.currentTimeMillis();

//        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_dev_login"));
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));
//        MysqlConn.executeUpdate(mysqlConn, "TRUNCATE TABLE device_mapping");

        InputStreamReader read = new InputStreamReader(new FileInputStream(new File("data/logistmap/result")));
        BufferedReader br = new BufferedReader(read);
        String lineTxt = null;
        String sql = "insert into device_mapping(type,devType,fakeSn,realSn,activeTime,merchant_id,merchant_name) values ";;
        String values = "";
        int index = 0;
        while((lineTxt = br.readLine()) != null) {
            String[] split = lineTxt.split("\\,");
            values += "(" + split[3] + "," + split[2] + ",'" + split[0] + "','" +
                    split[1] + "','" + split[4] + "'," + split[7] + ",'" + split[8] + "'),";

            if (++index % 5000 == 0) {
                MysqlConn.executeUpdate(mysqlConn, sql + values.substring(0, values.length()-1));
                values = "";
            }
        }
        MysqlConn.executeUpdate(mysqlConn, sql + values.substring(0, values.length()-1));

        System.out.println("total size is " + index);
        MysqlConn.close(mysqlConn);

        System.out.println("The job total cost " + (System.currentTimeMillis()-bgnTime)/1000 + "s");
    }
}
