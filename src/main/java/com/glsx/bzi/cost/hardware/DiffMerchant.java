package com.glsx.bzi.cost.hardware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 找出差异客户
 */

public class DiffMerchant {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("data/cost/hardware/sys")));
        TreeMap<String, Integer> merMaps = new TreeMap<>();
        String lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            merMaps.put(lineTxt.trim(), 1);
        }


        br = new BufferedReader(new FileReader(new File("data/delivery/cost/未抵消软件")));
        TreeMap<String, Integer> sysMaps = new TreeMap<>();
        lineTxt = null;
        while ((lineTxt= br.readLine()) != null) {
            sysMaps.put(lineTxt.split("\t")[1].trim(), 1);
        }


        int count = 0;
        for (Map.Entry<String, Integer> entry : sysMaps.entrySet()) {
            if (merMaps.get(entry.getKey()) == null) {
                System.out.println(entry.getKey());
                count++;
            }
        }

        br.close();

        System.out.println("count=" + count);
    }
}
