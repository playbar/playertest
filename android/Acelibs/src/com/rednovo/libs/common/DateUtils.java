package com.rednovo.libs.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具
 */
public class DateUtils {
    /**
     * 一天的毫秒数
     */
    public final static long ONE_DAY_MILLS = 24 * 60 * 60 * 1000;

    private static String d1;
    private static String d2;
    private static long day;
    private static long hour;
    private static long min;
    private static long s;
    private static SimpleDateFormat alldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * 获取当前时间的字符串 如：2014-11-03 11:11:11
     *
     * @return
     */
    public static String getCurrentDate() {
        return alldf.format(new Date());
    }

    /**
     * 格式化时间为： 刚刚 *秒前 *分钟前 *小时前
     *
     * @param date
     * @return
     */
    public static String getFormattedTime(String date) {
        Date now = new Date();
        d1 = alldf.format(now);
        d2 = date;
        getDiff();

        String time = "";
        try {
            if (day > 50 || day < 0) {
                time = d2;
            } else if (!(day == 0)) {
                time = day + "天前";
            } else if (!(hour == 0)) {
                time = hour + "小时前";
            } else if (!(min == 0)) {
                time = min + "分钟前";
            } else if (!(s == 0) && s >= 10) {
                time = s + "秒前";
            } else if (s >= -10 && s < 10) {
                time = "刚刚";
            } else {
                time = d2.substring(5, 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getFormattedTime(long date) {
        Date d = new Date(date);
        return getFormattedTime(alldf.format(d));
    }

    public static String getChatListTime(long date) {
        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar former_date = Calendar.getInstance(Locale.getDefault());
        former_date.setTimeInMillis(date);
        // 两个日期相差的毫秒数
        long d_time = now.getTimeInMillis() - former_date.getTimeInMillis();
        if (d_time < 60 * 1000) {// 一分钟内
            return "刚刚";
        } else if (d_time < 60 * 60 * 1000) {// 一小时内
            return (int) (d_time / (60 * 1000)) + "分钟前";
        } else if (d_time < getToday(now)) {// 今天
            return "今天" + Calendar2String(former_date, 12, 17);
        } else if (d_time < getToday(now) + ONE_DAY_MILLS) {// 昨天
            return "昨天" + Calendar2String(former_date, 12, 17);
        } else if (d_time < getToday(now) + 365 * ONE_DAY_MILLS) {// 前天
            return Calendar2String(former_date, 5, 17);
        } else {
            return Calendar2String(former_date, 0, 17);
        }
    }

    /**
     * 将Calendar格式化为字符串,并截取 #返回格式 2014年12月12日 12:12 全要：0,17 2014年12月12日 12:12 不要年:5,17 12月12日 12:12 只要时间: 12,17 12:12
     *
     * @param calendar
     * @param start    截取开始位置
     * @param end      截取结束位置
     * @return
     */
    private static String Calendar2String(Calendar calendar, int start, int end) {
        String time = calendar.get(Calendar.YEAR) + "年" + ((calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) + "月" : (calendar.get(Calendar.MONTH) + 1) + "月")
                + (calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) + "日 " : calendar.get(Calendar.DAY_OF_MONTH) + "日 ") + (calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" : calendar.get(Calendar.HOUR_OF_DAY) + ":")
                + (calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) : calendar.get(Calendar.MINUTE));
        return time.substring(start, end);
    }

    /**
     * 最近消息列表显示时间格式
     *
     * @param date
     * @return
     * @author Xd
     * @since 2015年5月8日下午7:33:39
     */
    public static String getRecentMsgTime(long date) {
        Calendar now = Calendar.getInstance(Locale.getDefault());
        Calendar former_date = Calendar.getInstance(Locale.getDefault());
        former_date.setTimeInMillis(date);
        // 两个日期相差的毫秒数
        long d_time = now.getTimeInMillis() - former_date.getTimeInMillis();
        if (d_time < getToday(now)) {// 今天
            return /* "今天"+ */Calendar2String(former_date, 12, 17);
        } else if (d_time < getToday(now) + 365 * ONE_DAY_MILLS) {
            return Calendar2String(former_date, 5, 11);
        } else {
            return Calendar2String(former_date, 0, 11);
        }
    }

    /**
     * 获得今天已过的毫秒数
     *
     * @param calendar
     * @return
     */
    private static long getToday(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 + calendar.get(Calendar.MINUTE) * 60 * 1000 + calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);
    }

    private static void getDiff() {
        Date now = new Date();
        Date date = new Date();
        try {
            now = alldf.parse(d1);
            date = alldf.parse(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long l = now.getTime() - date.getTime();
        day = l / (24 * 60 * 60 * 1000);
        hour = (l / (60 * 60 * 1000) - day * 24);
        min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
    }

    public static String getFormatDate(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 获得当前日期时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentFormatDate1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }

    /**
     * 转换时间
     *
     * @param second
     * @return
     */
    public static String converTime(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        return String.format("%02d:%02d:%02d", h, d, s);
    }
}
