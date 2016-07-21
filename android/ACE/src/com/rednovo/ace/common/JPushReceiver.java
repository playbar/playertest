package com.rednovo.ace.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.LiveInfo;
import com.rednovo.ace.net.parser.UserInfoResult;
import com.rednovo.ace.ui.activity.ACEWebActivity;
import com.rednovo.ace.ui.activity.MainActivity;
import com.rednovo.ace.ui.activity.live.LiveActivity;
import com.rednovo.ace.ui.activity.live.LiveRecordActivity;
import com.rednovo.libs.common.LogUtils;
import com.rednovo.libs.common.Utils;
import com.rednovo.libs.ui.base.AppManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Gaara on 16/5/4.
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        LogUtils.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            LogUtils.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            LogUtils.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtils.d(TAG, "[MyReceiver] 用户点击打开了通知");
            String msgId = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            JPushInterface.reportNotificationOpened(context, msgId);
            //TODO 跳转
            /**
             *  1.	只发alert   type:1
             *  2.	跳转页面    type:2 title:XX网页 url:www.aaaaa
             *  3.	跳直播间    type:3 user:user
             */
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            printBundle(bundle);
            if (!TextUtils.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    Intent i;
                    if (null != extraJson && extraJson.length() > 0) {
                        String type = extraJson.getString("type");
                        if ("3".equals(type)) {
                            try {
                                String content = extraJson.getString("user");
                                UserInfoResult.UserEntity user = JSON.parseObject(content, UserInfoResult.UserEntity.class);

                                i = new Intent(context, LiveActivity.class);
                                LiveInfo liveInfo = new LiveInfo();
                                liveInfo.setNickName(user.getNickName());
                                liveInfo.setProfile(user.getProfile());
                                liveInfo.setRank(user.getRank());
                                liveInfo.setSex(user.getSex());
                                liveInfo.setStarId(user.getUserId());
                                liveInfo.setShowId(user.getExtendData().getShowId());
                                liveInfo.setAudienceCnt("0");
                                liveInfo.setDownStreanUrl(user.getExtendData().getDownStreanUrl());
                                if (LiveInfoUtils.getStartId() != null && liveInfo.getStarId() != null && LiveInfoUtils.getStartId().equals(liveInfo.getStarId())) {
                                    return;
                                }
                                if(LiveInfoUtils.getIsShow())
                                    //如果自己正在直播，则不跳转
                                    return;
                                i.putExtra(Globle.KEY_LIVE_INFO, liveInfo);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            } catch (Exception ex) {

                            }
                        } else if ("2".equals(type)) {
                            String url = extraJson.getString("url");
                            if (!TextUtils.isEmpty(url)) {
                                i = new Intent(context, ACEWebActivity.class);
                                i.putExtra("url", url);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(i);
                            }
                        } else if ("1".equals(type)) {
                            //Nothing
                        }
                    }
                } catch (JSONException e) {

                }
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            LogUtils.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            LogUtils.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            LogUtils.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    LogUtils.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    LogUtils.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!Utils.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (null != extraJson && extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException e) {

                }

            }
            context.sendBroadcast(msgIntent);
        }
    }
}
