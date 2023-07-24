package com.glsx.bzi.logistmap;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

public class DetaMain {
    public static void main(String[] args) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data/logistmap/deta_mapping")));
        StringBuffer buffer = new StringBuffer();

        HashMap<String, Integer> result = new HashMap<>();
        InputStreamReader read = new InputStreamReader(new FileInputStream(new File("data/logistmap/result")));
        BufferedReader br = new BufferedReader(read);
        String lineTxt = null;
        // 8141221664,1790329158,5,8,2019-12-03 11:55:23.0,陕西,渭南,100899,卡仕达导航
        // fakesn,realsn,....
        while((lineTxt = br.readLine()) != null) {
            result.put(lineTxt.split("\\,")[0], 1);
        }

        read = new InputStreamReader(new FileInputStream(new File("data/logistmap/mapping")));
        br = new BufferedReader(read);
        lineTxt = null;
        int index = 0;
        int sumFake = 0;
        int sumDeta = 0;

        // 8,8140117023,2019-07-30 08:33:54.0,广东,广州,44202961,万达汽车服务中心
        // type,fakesn
        while((lineTxt = br.readLine()) != null) {
            sumFake++;
            if (result.get(lineTxt.split("\\,")[1]) != null) continue;
            sumDeta++;
            buffer.append(lineTxt).append("\n");
            if (++index % 500 == 0) {
                bw.write(buffer.toString());
                bw.flush();
                buffer.setLength(0);
            }
        }
        bw.write(buffer.toString());
        bw.flush();
        bw.close();

        System.out.println("sumFake=" + sumFake + ",result=" + result.size() + ",sumDeta=" + sumDeta + "," + result.size() + "=" + (sumFake-sumDeta));
    }
}
