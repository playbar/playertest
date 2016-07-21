package com.rednovo.ace.core.session;

import com.alibaba.fastjson.JSON;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.communication.Invoker;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.entity.Message;
import com.rednovo.libs.common.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * prj-name：ACE
 * fileName：${FILE_NAME}
 * Created by lizhen on Zhen.Li/02/29.
 * Copyright © 2016年 Rednovo. All rights reserved.
 *
 * @Version 1.0.0
 */
public class SessionPackageListener implements Invoker {
    private SessionPackParser sessionPackParser;
    private static final String LOG_TAG = "SessionPackageListener";

    public SessionPackageListener(SessionPackParser mSessionPackParser) {
        this.sessionPackParser = mSessionPackParser;
    }

    @Override
    public void onConnectFailed() {
        // TODO Auto-generated method stub
        LogUtils.i(LOG_TAG, "[client invoker] [连接失败]");
    }

    @Override
    public void onConnectSucessed(String sessionId) {
        // TODO Auto-generated method stub
        LogUtils.i(LOG_TAG, "[client invoker] [连接成功(" + sessionId + ")]");
        EventBus.getDefault().post(new BaseEvent(Globle.KEY_EVENT_SESSION_SUCCESS));
    }

    @Override
    public void onNewMessage(String sessionId, Message msg) {
        // TODO Auto-generated method stub
        String requestKey = msg.getSumy().getRequestKey();

        LogUtils.i(LOG_TAG, "requestKey=" + requestKey + " response=" + JSON.toJSONString(msg));

        if (Globle.KEY_SEND_MSG_STATUS.equals(requestKey)) {
            // 主动的消息互动
            sessionPackParser.onRecvMessage(msg);

        } else if (Globle.KEY_ENTER_ROOM.equals(requestKey)) {
            //进入房间
            sessionPackParser.onUserEnterRoomMsg(msg);
        } else if (Globle.KEY_INIT_ROOM_DATA.equals(requestKey)) {
            //初始化房间数据

        } else if (Globle.KEY_EXIT_ROOM.equals(requestKey)) {
            //退出房间
            sessionPackParser.onExitRoom(msg);
        } else if (Globle.KEY_KICK_OUT.equals(requestKey)) {
            //踢人
            sessionPackParser.onKickMsg(msg);
        } else if (Globle.KEY_LIVE_END_DATA.equals(requestKey)) {
            //直播实时数据(结束时展示)

        } else if (Globle.KEY_SEND_GIF.equals(requestKey)) {
            //送礼物
            sessionPackParser.onRecvGifMsg(msg);
        } else if (Globle.KEY_ROOM_BROADCAST.equals(requestKey)) {
            sessionPackParser.onRecvRoomBroadcast(msg);
        } else if(Globle.KEY_LIVE_CLOSE.equals(requestKey)){
            //直播结束
            sessionPackParser.onRecvLiveEnd(msg);
        }else if(Globle.KEY_ROOM_MSG.equals(requestKey)){
            //房间消息，分享，点赞，关注，系统消息
            sessionPackParser.onRecvRoomMsg(msg);
        } else if(Globle.KEY_FORBID.equals(requestKey)){
            //禁播
            sessionPackParser.liveForbid(msg);
        } else if(Globle.KEY_SEAL_NUM.equals(requestKey)){
            //封号
            sessionPackParser.sealNumber(msg);
        } else if(Globle.KEY_LIVE_PAUSE.equals(requestKey)){
            sessionPackParser.onLivePause(msg);
        }

    }

    @Override
    public void onReady() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSuspend(String arg0) {
        // TODO Auto-generated method stub
        LogUtils.i(LOG_TAG, "[client invoker] [会话中断]");
    }

}
