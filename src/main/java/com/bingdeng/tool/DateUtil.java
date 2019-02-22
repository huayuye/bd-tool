package com.bingdeng.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: Fran
 * @Date: 2019/1/9
 * @Desc:
 **/
public class DateUtil {

    private static final String default_DatePatten = "yyyy-MM-dd HH:mm:ss";
    private static final String DatePatten1 = "yyyy-MM-dd HH:mm";
    private static final String DatePatten2 = "yyyy-MM-dd";

    /**
     * 获取指定格式的时间字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToFormat(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 获取默认格式的时间字符串
     *
     * @param date
     * @return
     */
    public static String dateToDefaultFormat(Date date) {
        return dateToFormat(date, default_DatePatten);
    }

    /**
     * 获取指定格式的时间
     *
     * @param date
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date formatToDate(String date, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(date);
    }

    /**
     * 获取默认格式的时间
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static Date formatToDefaultDate(String date) throws ParseException {
        return formatToDate(date, default_DatePatten);
    }

    /**
     * 获取后n天的这个时间
     *
     * @param n
     * @return
     */
    public static Date getNextNDay(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, n);
        return calendar.getTime();
    }

    /**
     * 获取前n天的这个时间
     *
     * @param n
     * @return
     */
    public static Date getBeforeNDay(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 0 - n);
        return calendar.getTime();
    }

    /**
     * 获取后n小时的这个时间
     *
     * @param n
     * @return
     */
    public static Date getNextNHour(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, n);
        return calendar.getTime();
    }

    /**
     * 获取前n小时的这个时间
     *
     * @param n
     * @return
     */
    public static Date getBeforeNHour(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 0 - n);
        return calendar.getTime();
    }

    /**
     * 获取某天的后N天
     *
     * @param date
     * @param n
     * @return
     */
    public static Date getDateNextNHour(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, n);
        return calendar.getTime();
    }

    /**
     * 获取某天的前N天
     *
     * @param date
     * @param n
     * @return
     */
    public static Date getDateBeforeNHour(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 0 - n);
        return calendar.getTime();
    }

    /**
     * 获取前N分钟
     *
     * @param n
     * @return
     */
    public static Date getBeforeNMinu(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 0 - n);
        return calendar.getTime();
    }

    /**
     * 获取后N分钟
     *
     * @param n
     * @return
     */
    public static Date getNextNMinu(int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, n);
        return calendar.getTime();
    }

    /**
     * 获取某天的前N分钟
     *
     * @param date
     * @param n
     * @return
     */
    public static Date getDateBeforeNMinu(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 0 - n);
        return calendar.getTime();
    }

    /**
     * 获取某天的后N分钟
     *
     * @param date
     * @param n
     * @return
     */
    public static Date getDateNextNMinu(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, n);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        System.out.println( dateToDefaultFormat(getDateNextNHour(new Date(),25)));
    }

}
