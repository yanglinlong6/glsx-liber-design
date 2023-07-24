package com.glsx.bzi.cost.income;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * 对比成本
 */
public class DiffMain {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("data/cost/income/sys")));
        TreeMap<String, Double> sysMaps = new TreeMap<>();
        String lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String merchant = split[0].trim();
            double price = Double.valueOf(split[1]);
            int count = Integer.valueOf(split[2]);
            sysMaps.put(merchant, price);
        }

        br = new BufferedReader(new FileReader(new File("data/cost/income/汇总")));
        TreeMap<String, Double> aggMaps = new TreeMap<>();
        lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            String[] split = lineTxt.split("\t");
            String merchant = split[0].trim();
            double price = Double.valueOf(split[1]);
            int count = Integer.valueOf(split[2]);
            aggMaps.put(merchant, price);
        }

        for (Map.Entry<String, Double> entry : sysMaps.entrySet()) {
            if (aggMaps.get(entry.getKey()) == null) {
                System.out.println(entry.getKey() + "," + entry.getValue());
            }
        }
    }
}
