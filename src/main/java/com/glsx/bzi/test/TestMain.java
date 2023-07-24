package com.glsx.bzi.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TestMain {
    static class Detail {
        static int sellDetailGroup[][] = new int[51][48];
        static {
            for (int i = 0; i < sellDetailGroup.length; i++) {
                for (int j = 0; j < sellDetailGroup[i].length; j++) {
                    sellDetailGroup[i][j] = 0;
                }
            }
        }

        // 获取A客户第B月发货中第N月激活数
        public int getDetail(int i, int j) {
            return sellDetailGroup[i][j];
        }
    }


    public static void main(String[] args) throws IOException {
//        int sellDetailGroup[][] = new int[51][48];
        int sellTotalGroup[][] = new int[51][48];
        int activeTotalGroup[][] = new int[51][48];

        HashMap<Integer, Detail> detailMap = new HashMap<>();
        for (int i = 0; i < 51; i++) {
            detailMap.put(i,new Detail());
        }

        for (int i = 0; i < sellTotalGroup.length; i++) {
            for (int j = 0; j < sellTotalGroup[i].length; j++) {
//                sellDetailGroup[i][j] = 0;
                sellTotalGroup[i][j] = 0;
                activeTotalGroup[i][j] = 0;
            }
        }

        BufferedReader br = new BufferedReader(new FileReader(new File("data/src/渠道.txt")));
        int index = 0;
        String lineTxt = null;

        while ((lineTxt= br.readLine()) != null) {
            if (++index <3) continue;
            String[] split = lineTxt.split("\\,");
            for (int i = 2; i < split.length; i++) {
                if (i % 2 ==0) {
                    System.out.println("[" + (index - 3) + "][" + (i - 2)/2 + "]");
                    sellTotalGroup[index-3][(i-2)/2] = Integer.valueOf(split[i]);
                }
                else {
                    System.out.println("[" + (index - 3) + "][" + (i - 2)/2 + "]");
                    activeTotalGroup[index-3][(i-2)/2] = Integer.valueOf(split[i]);
                }
            }
            break;
        }
        br.close();

        // ---------------------------------------------------------------------
        // sellDetailGroup  每月发货中的激活数   sellDetailGroup[0][0]表示第一月发货中在第一月激活数   调整
        // sellTotalGroup   每月总发货数        sellTotalGroup[0][0]表示第一客户第一月发货总数       确定
        // activeTotalGroup 每月中激活数        activeTotalGroupp[0][0]表示第一客户第一月激活总数    确定






//        for (int row = 0; row < sellTotalGroup.size(); row++) {
//            int sumTmp = 0;
//            for (int col = row; col < sellTotalGroup.size(); col++) {
//                sumTmp += sellDetailGroup[row][col];
//            }
//            System.out.println(sellTotalGroup.get(row) + "=" + sumTmp);
//        }
    }
}
