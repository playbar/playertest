package com.rednovo.ace.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rednovo.ace.data.events.BaseEvent;

import de.greenrobot.event.EventBus;

/**
 * 定时刷新直播间观众列表的广播
 */
public class RefreshAudienceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new BaseEvent(Globle.KEY_ALARM_REQUEST_AUDIENCE));
    }
}
