package com.glsx.mongo;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * @author wangxw
 * @version 1.0.0
 * @ClassName CronExpressionUtil.java
 * @description: 定时任务策略（cron）表达式解析
 * @createTime 2021年10月30日 14:35:00
 */
public class CronExpressionUtil {

    /*
    spring的解析方式
    public static Long getNextFireTime(String cronExpression, Date nowDate) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronExpression);
        Date nextDate = cronSequenceGenerator.next(nowDate);
        return nextDate.getTime();
    }
    */

    /**
     * quartz解析方式
     * @param cronExpression
     * @param nowDate
     * @return
     * @throws ParseException
     */
    public static Long getNextFireTime(String cronExpression, Date nowDate) throws ParseException {
        CronExpression expression = new CronExpression(cronExpression);
        try {
            Date nextDate = expression.getNextValidTimeAfter(nowDate);
            return nextDate.getTime();
        } catch (Exception e) {
            throw new IllegalArgumentException("cronExpression cannot be validation, cron:" + cronExpression);
        }
    }

    public static void main(String[] args) throws ParseException {
        Date nowDate = new Date();
        /*
            未来执行时间：
            2022-10-26 02:20:00
            2023-10-26 02:20:00
            2024-10-26 02:20:00
            2025-10-26 02:20:00
            2026-10-26 02:20:00
            2027-10-26 02:20:00
            2028-10-26 02:20:00
            2029-10-26 02:20:00
         */
        Long nextTime = getNextFireTime("0 20 04 * * ?", new Date());
        System.out.println(nextTime);

        System.out.println(new Date(nextTime));

        CronExpression expression = new CronExpression("24 01 11 10 12 ? 2021");
        // 最后一次执行时间
        Date finalFireTime = expression.getFinalFireTime();
        System.out.println(finalFireTime);
//        printBean(finalFireTime, "finalFireTime");

        // 下一个执行时间
        Date nextValidTimeAfter = expression.getNextValidTimeAfter(nowDate);
        System.out.println(nextValidTimeAfter);
//        printBean(nextValidTimeAfter, "nextValidTimeAfter");

        // 下一个无效时间
        Date nextInvalidTimeAfter = expression.getNextInvalidTimeAfter(nowDate);
        System.out.println(nextInvalidTimeAfter);
//        printBean(nextInvalidTimeAfter, "nextInvalidTimeAfter");

        // 下一个时间
        //Date timeAfter = expression.getTimeAfter(nowDate);
        System.out.println(nowDate);
//        printBean(timeAfter, "timeAfter");

        String cronExpress = expression.getCronExpression();
        System.out.println("cronExpression is:" + cronExpress);

        String[] cronArray = cronExpress.split(" ");
        System.out.println("cron index 6:" + cronArray[6]);


        CronExpression expression2 = new CronExpression("24 01 11 10 12 ?");
        // 最后一次执行时间
        Date finalFireTime2 = expression2.getNextValidTimeAfter(nowDate);
//        printBean(finalFireTime2, "finalFireTime2");

    }

    private static void printBean(Date finalFireTime, String desc) {
        if (finalFireTime!=null) {
//            System.out.println(desc + " result:" + DateFormatUtil.toString(finalFireTime, DateFormatUtil.pattern19));
        } else {
            System.out.println(desc + " result is NULL....");
        }
    }
}
