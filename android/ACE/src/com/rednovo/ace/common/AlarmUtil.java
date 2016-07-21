package com.rednovo.ace.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.rednovo.libs.BaseApplication;

/**
 * Created by lizhen on 16/3/15.
 */
public class AlarmUtil {

    public static AlarmManager getAlarmManager(Context ctx) {
        return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    }

    public static void alarm(int renew_time, Class<?> cls) {
        Context ctx = BaseApplication.getApplication().getApplicationContext();
        Intent intent = new Intent(ctx, cls);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
        if (renew_time > 0) {
            // if (Utils.isMIUI() || Utils.isGT19228()) {
            // am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + renew_time * 1000, sender);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + renew_time * 1000, renew_time * 1000, sender);
//            } else {
//            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + renew_time * 1000, sender);
//            }
        }
    }
}
