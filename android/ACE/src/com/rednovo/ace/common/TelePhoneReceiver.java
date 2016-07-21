package com.rednovo.ace.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.rednovo.ace.data.events.PhoneStateEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by lizhen on 16/3/10.
 */
public class TelePhoneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(extraState) || TelephonyManager.EXTRA_STATE_OFFHOOK.equals(extraState)) {
            EventBus.getDefault().post(new PhoneStateEvent(Globle.KEY_EVENT_PHONE_STATE));
        } else if (TelephonyManager.EXTRA_STATE_IDLE.equals(extraState)) {
            // do nothing
        }
    }


}
