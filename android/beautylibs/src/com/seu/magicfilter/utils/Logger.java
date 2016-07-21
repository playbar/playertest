package com.seu.magicfilter.utils;

import android.util.Log;

/**
 * @author Zhen.Li
 * @since 2016-05-21
 */
public class Logger {
    /**
     * 日志输出级别NONE
     */
    public static final int LEVEL_NONE = 0;
    /**
     * 日志输出级别V
     */
    public static final int LEVEL_VERBOSE = 1;
    /**
     * 日志输出级别D
     */
    public static final int LEVEL_DEBUG = 2;
    /**
     * 日志输出级别I
     */
    public static final int LEVEL_INFO = 3;
    /**
     * 日志输出级别W
     */
    public static final int LEVEL_WARN = 4;
    /**
     * 日志输出级别E
     */
    public static final int LEVEL_ERROR = 5;

    /**
     * 是否允许输出log
     */
    public static int mDebuggable = LEVEL_ERROR;

    /**
     * 用于记时的变量
     */
    private static long mTimestamp = 0;
    /**
     * 写文件的锁对象
     */
    private static final Object mLogLock = new Object();

    private static final String LOG_TAG = "Ace";


    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(String tag, String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(String tag, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(String tag, String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(String tag, String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }

    /**
     * 以级别为 w 的形式输出Throwable
     */
    public static void w(String tag, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, "", tr);
        }
    }

    /**
     * 以级别为 w 的形式输出LOG信息和Throwable
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(tag, msg, tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String tag, String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(String tag, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, "", tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(tag, msg, tr);
        }
    }
}
