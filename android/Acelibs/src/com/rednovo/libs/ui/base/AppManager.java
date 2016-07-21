package com.rednovo.libs.ui.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;

import com.rednovo.libs.common.CacheUtils;
import com.rednovo.libs.common.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Stack;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 *
 * @author Xd/2015年5月11日
 */
public class AppManager {
    private Stack<WeakReference<BaseActivity>> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    /**
     * 单实例 , UI无需考虑多线程同步问题
     */
    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到栈
     *
     * @param activity
     */
    public void addActivity(BaseActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<WeakReference<BaseActivity>>();
        }
        WeakReference<BaseActivity> ac = new WeakReference<BaseActivity>(activity);
        activityStack.add(ac);
    }

    /**
     * 获取当前Activity（栈顶Activity）
     */
    public BaseActivity currentActivity() {
        if (activityStack == null || activityStack.isEmpty()) {
            return null;
        }
        BaseActivity activity = activityStack.lastElement().get();
        if(activity == null){
            activityStack.remove(activityStack.lastElement());
            currentActivity();
        }
        return activity;
    }

    /**
     * 获取当前Activity（栈顶Activity） 没有找到则返回null
     */
    public BaseActivity findActivity(Class<?> cls) {
        BaseActivity activity = null;
        for (WeakReference<BaseActivity> w : activityStack) {
            if (w.get() != null && w.get().getClass().equals(cls)) {
                activity = w.get();
                break;
            }
        }
        return activity;
    }

    /**
     * 结束当前Activity（栈顶Activity）
     */
    public void finishActivity() {
        BaseActivity activity = activityStack.lastElement().get();
        if(activity != null)
            finishActivity(activity);
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(BaseActivity activity) {
        if(activity == null)
            return;
        WeakReference<BaseActivity> ac = null;
        for (WeakReference<BaseActivity> w : activityStack) {
            if (w.get() != null && w.get().getClass().equals(activity.getClass())) {
                ac = w;
                activity.finish();
                activity = null;
                break;
            }
        }
        if(ac != null){
            activityStack.remove(ac);
        }
    }

    /**
     * 移除队列中的activity
     */
    public void removeActivity(BaseActivity activity) {
        if(activity == null)
            return;
        WeakReference<BaseActivity> ac = null;
        for (WeakReference<BaseActivity> w : activityStack) {
            if (w.get() != null && w.get().getClass().equals(activity.getClass())) {
                ac = w;
                activity = null;
                break;
            }
        }
        if(ac != null){
            activityStack.remove(ac);
        }
    }

    /**
     * 结束指定的Activity(重载)
     */
    public void finishActivity(Class<?> cls) {
        BaseActivity targetActivity = null;
        for (WeakReference<BaseActivity> w : activityStack) {
            if (w.get() != null && w.get().getClass().equals(cls.getClass())) {
                targetActivity = w.get();
                break;
            }
        }
        if (targetActivity != null) {
            finishActivity(targetActivity);
        }
    }

    /**
     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
     *
     * @param cls
     */
    public void finishOthersActivity(Class<?> cls) {
        for (WeakReference<BaseActivity> w : activityStack) {
            if (!(w.get() != null && w.get().getClass().equals(cls.getClass()))) {
                finishActivity(w.get());
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i).get()) {
                activityStack.get(i).get().finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 应用程序退出
     */
    public void appExit(Context context) {
        try {
            // 为了Umeng统计的准确，关闭应用前调用
			MobclickAgent.onKillProcess(context);
            finishAllActivity();
            CacheUtils.close();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            System.exit(0);
        }
    }

    /**
     * 根据包名，启动该应用的默认Activity
     *
     * @param ctx
     * @param packageName
     */
    public static void startApkActivity(final Context ctx, String packageName) {
        PackageManager pm = ctx.getPackageManager();
        PackageInfo pi;
        try {
            pi = pm.getPackageInfo(packageName, 0);
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setPackage(pi.packageName);
            List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                String className = ri.activityInfo.name;
                intent.setComponent(new ComponentName(packageName, className));
                ctx.startActivity(intent);
            }
        } catch (NameNotFoundException e) {
            LogUtils.e("startActivity", e.getMessage());
        }
    }


}
