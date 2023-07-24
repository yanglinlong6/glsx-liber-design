package com.glsx.bzi.cost.hardware;

import com.glsx.conf.ConfigurationManager;
import com.glsx.connection.MysqlConn;
import com.glsx.util.DateUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 硬件成本相关，按照渠道或者直销将匹配上的sn全部入库
 */

public class HardwareCostMain {
    public static void main(String[] args) throws IOException {
        Connection mysqlConn = MysqlConn.getConnection(ConfigurationManager.getProperty("mysql_prd_login"));
        // 按照渠道或直销分别处理入库数据
        //       String type = "直销";
        String type = "代销";
//        String type = "广汇";

        // 结算客户分类
        HashMap<String, String> classifyMap = classifyMerchant(mysqlConn, type);

        // base1 ：成本维度指标计算
        // base1.1 客户|年月 -> 成本
//        Map<String, Double> merMonPriceMap = getMerMonPriceMap(classifyMap);
        // base1.2 年月 -> 成本  monPriceMap
        // base1.3 客户 -> 成本  merPriceMap
//        Map<String, Double> monPriceMap = new TreeMap<>();
//        Map<String, Double> merPriceMap = new TreeMap<>();
//        getMutiPriceMap(merMonPriceMap, monPriceMap, merPriceMap);

        // 年月 -> 成本  monPriceMap
        Map<String, Double> monPriceMap = getMonPriceMap(classifyMap);

        // base2：设备维度指标计算
        // base2.1 客户|年月 -> 数量 merMonCountMap
        Map<String, Integer> merMonCountMap = getMerMonCountMap(mysqlConn, classifyMap);
        // base2.2 年月 -> 数量  monCountMap
        // base2.3 客户 -> 数量  merCountMap
        Map<String, Integer> monCountMap = new TreeMap<>();
        Map<String, Integer> merCountMap = new TreeMap<>();
        getMutiCountMap(merMonCountMap, monCountMap, merCountMap, classifyMap);

        // base2.4 年月 -> sn1,sn2,sn3..... monsnsMap
        Map<String, String> monSnsMap = getMerMonSnsMap(mysqlConn, classifyMap);

        // base3 每月平均成本 根据monPriceMap（年月 -> 成本）和monCountMap（年月 -> 数量）
        Map<String, Double> monAvgPriceMap = getMonAvgPrice(monPriceMap, monCountMap);
        // 渠道这两个月单独处理
//        if (type.equals("直销")) {
//            monAvgPriceMap.put("2017-09", 131.1879687);
//            monAvgPriceMap.put("2017-10", 131.1879687);
//        }

        //直销这两个月单独处理
        if (type.equals("直销")) {
            monAvgPriceMap.put("2017-09", 114.24563812);
            monAvgPriceMap.put("2017-10", 114.24563812);
        }

        // base4 匹配上每个sn的价格 输出每个sn的安装单价 snPriceMap(sn->price)
        TreeMap<String, Double> snPriceMap = new TreeMap<>();
        for (Map.Entry<String, Double> entry : monAvgPriceMap.entrySet()) {
            String key = entry.getKey();
            double price = entry.getValue();
            String[] sns = monSnsMap.get(key).split("\\,");
            int snLen = sns.length;
            for (int i = 0; i < snLen; i++) {
                snPriceMap.put(sns[i], price);
            }
        }

        // base 5 入库  sn,结算客户,结算时间,激活时间,价格,期限,类型(渠道/直销)
        insertMysql(mysqlConn, snPriceMap, type);

        // stat1:按成本维度统计  根据merMonPriceMap（客户|年月 -> 成本）和merMonCountMap（客户|年月 -> 数量）和monAvgPriceMap
//        stat1(merMonPriceMap, merMonCountMap, monAvgPriceMap);
//        if (1 == 1) return;

        // stat2:按设备维度统计
//        stat2(merMonPriceMap, merMonCountMap, monAvgPriceMap);
//        if (1 == 1) return;

        MysqlConn.close(mysqlConn);
    }

    private static Map<String, Double> getMonPriceMap(HashMap<String, String> classifyMap) throws IOException {
        double priceTotal = 0;
        double priceTypeTotal = 0;
        Map<String, Double> monPriceMap = new TreeMap<>();
//        TreeMap<String, Double> basePriceMap = new TreeMap<>();

//        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/hardwareCost")));
        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/未抵消软件")));
//        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/抵消软件")));
//        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/安装成本_更新")));

        String lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String settle_merchant = split[1].trim();
            String monTime = split[6].trim();
            if (split[7].trim().equals("-") || split[7].trim().equals("")) continue;

            double price = Double.valueOf(split[7].trim());
            priceTotal += price;
//            String key = settle_merchant + "|" + monTime;

            if (classifyMap.get(settle_merchant) == null) continue;

            priceTypeTotal += price;

            if (monPriceMap.get(monTime) == null)
                monPriceMap.put(monTime, price);
            else
                monPriceMap.put(monTime, monPriceMap.get(monTime) + price);
        }
        System.out.println("础数据简单汇总处理,priceTotal=" + priceTotal + ",priceTypeTotal=" + priceTypeTotal);

        return monPriceMap;
    }

    private static void insertMysql(Connection mysqlConn, TreeMap<String, Double> snPriceMap, String type) {
        String sql = "SELECT sn,settle_time,active_time,settle_merchant,pk_period from dj_device_details where settle_time is not null";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                String tmpSql = "insert into t_hardware_cost(sn,settle_time,active_time,settle_merchant,period,price,mer_type) values";
                String values = "";

                int sumCount = 0;
                while (rs.next()) {
                    String sn = rs.getString(1);
                    if (snPriceMap.get(sn) == null) continue;
                    double price = snPriceMap.get(sn);

                    values += "('" + sn + "','" + rs.getString(2) + "','" +
                            rs.getString(3) + "','" + rs.getString(4) + "'," + rs.getInt(5) + "," + price +
                            ",'" + type + "'),";

                    if (++sumCount % 3000 == 0) {
                        tmpSql += values.substring(0, values.length()-1);
                        MysqlConn.executeUpdate(mysqlConn, tmpSql);
                        tmpSql = "insert into t_hardware_cost(sn,settle_time,active_time,settle_merchant,period,price,mer_type) values";
                        values = "";
                    }
                }

                if (!values.equals("")) {
                    tmpSql += values.substring(0, values.length()-1);
                    MysqlConn.executeUpdate(mysqlConn, tmpSql);
                }
            }
        });

    }

    private static Map<String, String> getMerMonSnsMap(Connection mysqlConn, HashMap<String, String> classifyMap) {
        Map<String, String> monsnsMap = new TreeMap<>();
        String sql = "SELECT settle_merchant,DATE_FORMAT(settle_time,'%Y-%m'),sn FROM dj_device_details\n" +
                "WHERE settle_time IS NOT NULL";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String settle_merchant = rs.getString(1).trim();
                    if (classifyMap.get(settle_merchant) == null) continue;

                    String monTime = rs.getString(2);
                    if (settle_merchant.equals("广汇汽车")) {
                        monTime = DateUtils.getAfterMonth3(monTime);
                    }
                    String sn = rs.getString(3).trim();
                    if (monsnsMap.get(monTime) == null)
                        monsnsMap.put(monTime, sn);
                    else
                        monsnsMap.put(monTime, monsnsMap.get(monTime) + "," + sn);
                }
            }
        });

        return monsnsMap;
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

    private static void stat2(Map<String, Double> merMonPriceMap, Map<String, Integer> merMonCountMap, Map<String, Double> monAvgPriceMap) {
        for (Map.Entry<String, Integer> entry : merMonCountMap.entrySet()) {
            String monTime = entry.getKey().split("\\|")[1];

//            if (entry.getKey().equals("广州盟锐科技发展有限公司|2020-08"))
//                System.out.println();

            if (merMonPriceMap.get(entry.getKey()) == null) {
//                if (monTime.compareTo("2021") < 0 && monTime.compareTo("2017-07") > 0)
                  if (monTime.compareTo("2021") < 0 && monTime.compareTo("2017-01") > 0)
                    System.out.println(entry.getKey() + "|" + entry.getValue() + "|-|" + monAvgPriceMap.get(monTime)*entry.getValue());
            }
            else {
                System.out.println(entry.getKey() + "|" + entry.getValue() + "|" +
                        merMonPriceMap.get(entry.getKey()) + "|" + monAvgPriceMap.get(monTime) * entry.getValue());
            }
        }
    }

    private static void stat1(Map<String, Double> merMonPriceMap, Map<String, Integer> merMonCountMap, Map<String, Double> monAvgPriceMap) {
        for (Map.Entry<String, Double> entry : merMonPriceMap.entrySet()) {
            String monTime = entry.getKey().split("\\|")[1];

            if (merMonCountMap.get(entry.getKey()) == null)
                System.out.println(entry.getKey() + "|" + entry.getValue() + "|" +
                        "-|-");
            else
                System.out.println(entry.getKey() + "|" + entry.getValue() + "|" +
                        merMonCountMap.get(entry.getKey()) + "|" + merMonCountMap.get(entry.getKey())*monAvgPriceMap.get(monTime));
        }
    }

    private static Map<String, Double> getMonAvgPrice(Map<String, Double> monPriceMap, Map<String, Integer> monCountMap) {
        Map<String, Double> monAvgPriceMap = new TreeMap<>();

        for (Map.Entry<String, Double> entry : monPriceMap.entrySet()) {
            if (monCountMap.get(entry.getKey()) == null)
                System.out.println("错误" + entry.getKey());
            else {
//                System.out.println(entry.getKey() + "|" + entry.getValue() + "|" + monCountMap.get(entry.getKey()) + "|" +
//                        entry.getValue()/monCountMap.get(entry.getKey()));
                monAvgPriceMap.put(entry.getKey(), entry.getValue()/monCountMap.get(entry.getKey()));
            }
        }

        return monAvgPriceMap;
    }

    private static void getMutiCountMap(Map<String, Integer> merMonCountMap, Map<String, Integer> monCountMap, Map<String, Integer> merCountMap, HashMap<String, String> classifyMap) {
        for (Map.Entry<String, Integer> entry : merMonCountMap.entrySet()) {
            String[] split = entry.getKey().split("\\|");
            String merchant = split[0].trim();
            if (classifyMap.get(merchant) == null) continue;

            String monTime = split[1];
            int count = entry.getValue();

            // 客户维
            if (merCountMap.get(merchant) == null)
                merCountMap.put(merchant, count);
            else
                merCountMap.put(merchant, merCountMap.get(merchant)+count);

            // 时间维
            if (monCountMap.get(monTime) == null)
                monCountMap.put(monTime, count);
            else
                monCountMap.put(monTime, monCountMap.get(monTime)+count);
        }
    }

    private static Map<String, Integer> getMerMonCountMap(Connection mysqlConn, HashMap<String, String> classifyMap) {
        Map<String, Integer> merMonCountMap = new TreeMap<>();

        String sql = "SELECT settle_merchant,DATE_FORMAT(settle_time,'%Y-%m'),count(1) FROM dj_device_details\n" +
                "WHERE settle_time IS NOT NULL GROUP BY settle_merchant,DATE_FORMAT(settle_time,'%Y-%m')";
        MysqlConn.executeQuery(mysqlConn, sql, new MysqlConn.QueryCallback() {
            @Override
            public void process(ResultSet rs) throws Exception {
                while (rs.next()) {
                    String settle_merchant = rs.getString(1).trim();
                    if (classifyMap.get(settle_merchant) == null) continue;
                    String monTime = rs.getString(2);
                    if (settle_merchant.equals("广汇汽车")) {
                        monTime = DateUtils.getAfterMonth3(monTime);
                    }

                    int count = rs.getInt(3);
                    merMonCountMap.put(settle_merchant + "|" + monTime, count);
                }
            }
        });

        return merMonCountMap;
    }

    private static void getMutiPriceMap(Map<String, Double> merMonPriceMap, Map<String, Double> monPriceMap, Map<String, Double> merPriceMap) {
        for (Map.Entry<String, Double> entry : merMonPriceMap.entrySet()) {
            String[] split = entry.getKey().split("\\|");
            String merchant = split[0];
            String monTtime = split[1];
            double price = entry.getValue();
            // 客户维
            if (merPriceMap.get(merchant) == null)
                merPriceMap.put(merchant, price);
            else
                merPriceMap.put(merchant, merPriceMap.get(merchant)+price);

            // 时间维
            if (monPriceMap.get(monTtime) == null)
                monPriceMap.put(monTtime, price);
            else
                monPriceMap.put(monTtime, monPriceMap.get(monTtime)+price);
        }
    }

    private static Map<String, Double> getMerMonPriceMap(HashMap<String, String> classifyMap) throws IOException {
        // 客户|年月 -> 成本  注:不区分直销/渠道的总成本
        Map<String, Double> merMonPriceTotalMap = new TreeMap<>();

        // 1 基础数据简单汇总处理
        // 客户|年月 -> 成本 （根据表格简单处理）
        TreeMap<String, Double> basePriceMap  = getBasePriceMap();

        // 累计值为负数的客户
//        negativeMerchant(basePriceMap);

        // 2 处理退货，往前推

        // 客户 -> 年月,成本|年月,成本|年月,成本|年月,成本...
        HashMap<String, String> merMonPricestMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : basePriceMap.entrySet()) {
            String[] key = entry.getKey().split("\\|");
            String metchant = key[0].trim();
            String monTime = key[1];

            double price = entry.getValue();
            if (merMonPricestMap.get(metchant) == null)
                merMonPricestMap.put(metchant, monTime + "," + price);
            else
                merMonPricestMap.put(metchant, (monTime + "," + price) + "|" + merMonPricestMap.get(metchant));
        }

        for (Map.Entry<String, String> entry : merMonPricestMap.entrySet()) {
            String metchant = entry.getKey();
            String[] detailInto = entry.getValue().split("\\|");
            int size = detailInto.length;

            double diffPrice = 0;
            for (int i = 0; i < size; i++) {
                String[] split = detailInto[i].split("\\,");
                String monTime = split[0];
                Double price = Double.valueOf(split[1]) + diffPrice;
                if (price < 0) {
                    diffPrice = price;
                } else {
//                    System.out.println(metchant + "|" + monTime + "->" + price);
                    diffPrice = 0;
                    merMonPriceTotalMap.put(metchant + "|" + monTime, price);
                }
            }
        }

        // 处理退货后，成本总额测试
        runTestPrice(merMonPriceTotalMap);
        
        // 成本前后对比
//        runTest(merMonPriceMap, basePriceMap);


        // 客户|年月 -> 成本  注:区分直销/渠道的总成本
        Map<String, Double> merMonPriceMap = new TreeMap<>();
        double sumPrice = 0;
        for (Map.Entry<String, Double> entry : merMonPriceTotalMap.entrySet()) {
            String mer = entry.getKey().split("\\|")[0].trim();
            if (classifyMap.get(mer) == null) continue;
            sumPrice += entry.getValue();
            merMonPriceMap.put(entry.getKey(), entry.getValue());
        }

        System.out.println("针对渠道或直销的成本,sumPrice=" + sumPrice);
        return merMonPriceMap;
    }

    private static void runTest(Map<String, Double> merMonPriceMap, TreeMap<String, Double> basePriceMap) {
        HashMap<String, Double> map1 = new HashMap<>();
        for (Map.Entry<String, Double> entry : basePriceMap.entrySet()) {
            String key = entry.getKey().split("\\|")[0];
            double price = entry.getValue();
            if (map1.get(key) == null)
                map1.put(key, price);
            else
                map1.put(key, map1.get(key) + price);
        }

        HashMap<String, Double> map2 = new HashMap<>();
        for (Map.Entry<String, Double> entry : merMonPriceMap.entrySet()) {
            String key = entry.getKey().split("\\|")[0];
            double price = entry.getValue();
            if (map2.get(key) == null)
                map2.put(key, price);
            else
                map2.put(key, map2.get(key) + price);
        }

        for (Map.Entry<String, Double> entry : map1.entrySet()) {
            if (map2.get(entry.getKey()) == null)
                System.out.println("异常");
            else {
                if (entry.getValue()-map2.get(entry.getKey()) != 0)
                    System.out.println(entry.getKey() + "|" + entry.getValue() +
                            "|" + map2.get(entry.getKey()) + "|" + (entry.getValue()-map2.get(entry.getKey())));
            }
        }
    }

    private static void runTestPrice(Map<String, Double> merMonPriceMap) {
        double priceTotal = 0;
        for (Map.Entry<String, Double> entry : merMonPriceMap.entrySet()) {
            priceTotal += entry.getValue();
        }
        System.out.println("处理退货后，成本总额priceTotal=" + priceTotal);
    }

    private static void negativeMerchant(TreeMap<String, Double> basePriceMap) {
        HashMap<String, Double> merchantMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : basePriceMap.entrySet()) {
            String merchant = entry.getKey().split("\\|")[0];
            if (merchantMap.get(merchant) == null)
                merchantMap.put(merchant, entry.getValue());
            else
                merchantMap.put(merchant, merchantMap.get(merchant) + entry.getValue());
        }

        for (Map.Entry<String, Double> entry : merchantMap.entrySet()) {
            if (entry.getValue().doubleValue() < 0)
                System.out.println(entry.getKey() + "," + entry.getValue());
        }
    }

    // 根据表格hardwareCost基础数据简单汇总处理
    private static TreeMap<String, Double> getBasePriceMap() throws IOException {
        double priceTotal = 0;
        TreeMap<String, Double> basePriceMap = new TreeMap<>();

//        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/hardwareCost")));
        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/未抵消软件")));
//        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/抵消软件")));

//        BufferedReader br = new BufferedReader(new FileReader(new File("data/delivery/cost/安装成本_更新")));

        String lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String settle_merchant = split[1].trim();
            String monTime = split[6].trim();
            if (split[7].trim().equals("-") || split[7].trim().equals("")) continue;

            double price = Double.valueOf(split[7].trim());
            priceTotal += price;
            String key = settle_merchant + "|" + monTime;
            if (basePriceMap.get(key) == null)
                basePriceMap.put(key, price);
            else
                basePriceMap.put(key, basePriceMap.get(key) + price);
        }

        System.out.println("础数据简单汇总处理,priceTotal=" + priceTotal);

        return basePriceMap;
    }
}
