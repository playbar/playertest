package com.rednovo.libs;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;

import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.Constant;
import com.rednovo.libs.common.ShowUtils;
import com.rednovo.libs.common.StorageUtils;
import com.rednovo.libs.common.ThirdPartyAPI;
import com.rednovo.libs.net.fresco.FrescoEngine;
import com.rednovo.libs.ui.base.AppManager;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class BaseApplication extends Application {

    private static BaseApplication mApplication;
    private ActivityManager activityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        AnalyticsConfig.setAppkey(this, Constant.UMENG_APPKEY);
        MobclickAgent.openActivityDurationTrack(false);
        AnalyticsConfig.enableEncrypt(true);
        //初始化Fresco
        FrescoEngine.init(this);
        //缓存类
        CacheUtils.init(this);
        //显示类
        ShowUtils.init(this);
        //第三方api
        ThirdPartyAPI.init(getApplicationContext());
        activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        StorageUtils.initSharePreferences(this);
    }

    /**
     * 应用是否到后台
     *
     * @return
     */
    public boolean isBackground() {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return true;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(this.getPackageName())
                    && (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        FrescoEngine.clearMemory();

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    public static BaseApplication getApplication() {
        return mApplication;
    }

    public void appExit() {
        AppManager.getAppManager().appExit(this);
        android.os.Process.killProcess(Process.myPid());
    }
}
