package com.rednovo.libs.common;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.rednovo.libs.BaseApplication;

/**
 * @author zhen.Li/2015-4-26
 */
public class NetWorkUtil {
    private static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    /**
     * 得到联网类型
     *
     * @param
     * @return ConnectivityManager.TYPE_MOBILE 或者 ConnectivityManager.TYPE_WIFI
     */
    public static int getNetworkType(Context context) {
        return getNetworkInfo(context).getType();
    }

    /**
     * 得到详细网络类型
     *
     * @param
     * @return TelephonyManager.NETWORK_TYPE_EDGE 或 TelephonyManager.NETWORK_TYPE_UMTS 等
     */
    public static int getNetworkSubtype(Context context) {
        return getNetworkInfo(context).getSubtype();
    }

    /**
     * 检测网络连接是否可用
     *
     * @param
     * @return true 可用; false 不可用
     */
    public static boolean isNetworkAvailable() {
        Context context = BaseApplication.getApplication().getApplicationContext();
        NetworkInfo netInfo = getNetworkInfo(context);
        if (netInfo != null) {
            return netInfo.isAvailable();
        }
        return false;
    }

    public static boolean isAvailable() {
        Context context = BaseApplication.getApplication().getApplicationContext();
        NetworkInfo netInfo = getNetworkInfo(context);
        if (netInfo != null) {
            return netInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断网络是否正常
     *
     * @param
     * @return
     * @author zhen.Li
     * @since 2015-4-26下午11:56:11
     */
    public static boolean checkNetwork() {
        if (!isNetworkAvailable()) {
            ShowUtils.showToast("请检测您的网络是否正常");
            return false;
        } else
            return true;

    }

    /**
     * 跳转到系统网络设置界面
     *
     * @param
     * @author zhen.Li
     * @since 2015-4-26下午11:52:51
     */
    public static void jump2NetworkSetting() {
        Context ctx = BaseApplication.getApplication().getApplicationContext();
        if (android.os.Build.VERSION.SDK_INT > 13) {
            ctx.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            ctx.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

}
