package com.glsx.main;

import com.glsx.conf.ConfigurationManager;
import com.glsx.pool.MysqlConnPool;

import java.sql.*;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MysqlTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException {
        int size = 10;
        MysqlConnPool mysqlConnPool = MysqlConnPool.getInstance(ConfigurationManager.getProperty("testUrl"));
        ExecutorService pool = Executors.newFixedThreadPool(size);
        for (int i = 0; i < size; i++) {
            pool.submit(new MyThread(mysqlConnPool));
        }

        pool.shutdown();
        while (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
//            System.out.println("mysqlConnPool.size())=" + mysqlConnPool.size());
        }
    }

    private static class MyThread implements Runnable {
        private MysqlConnPool mysqlConnPool;

        public MyThread(MysqlConnPool mysqlConnPool) {
            this.mysqlConnPool = mysqlConnPool;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " is starting...");
                int time = new Random().nextInt(60000);
                Thread.sleep(time);

                mysqlConnPool.executeQuery("select * from file_info", null, new MysqlConnPool.QueryCallback() {
                    @Override
                    public void process(ResultSet rs) throws Exception {
                        System.out.println("-----------");
                    }
                });

                System.out.println(Thread.currentThread().getName() + " is ended, time=" + time + ",mysqlConnPool.size()=" + mysqlConnPool.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
