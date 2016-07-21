package com.rednovo.ace.core.session;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.AceApplication;
import com.rednovo.ace.R;
import com.rednovo.ace.common.GiftUtils;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.data.UserInfoUtils;
import com.rednovo.ace.data.cell.MsgLog;
import com.rednovo.ace.data.events.BaseEvent;
import com.rednovo.ace.data.events.ChatMessage;
import com.rednovo.ace.data.events.EnterRoomEvent;
import com.rednovo.ace.data.events.KickOutEvent;
import com.rednovo.ace.data.events.LiveEndEvent;
import com.rednovo.ace.data.events.LivePauseEvent;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.data.events.RoomBroadcastEvent;
import com.rednovo.ace.data.events.SealNumberEvent;
import com.rednovo.ace.data.events.SendGiftResponse;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.net.parser.GiftListResult;
import com.rednovo.libs.common.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * prj-name：ACE
 * fileName：${FILE_NAME}
 * Created by lizhen on Zhen.Li/02/29.
 * Copyright © 2016年 rednovo. All rights reserved.
 *
 * @Version 1.0.0
 */
public class SessionPackParser {

    private Context mContext;

    public SessionPackParser() {
        mContext = AceApplication.getApplication().getApplicationContext();
    }

    /**
     * 接收消息
     *
     * @param msg
     */
    public void onRecvMessage(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        String chatMode = msg.getSumy().getChatMode();
        String duration = msg.getSumy().getDuration();
        String msgType = msg.getSumy().getMsgType();
        String sendId = msg.getSumy().getSenderId();
        String groupId = msg.getSumy().getShowId();
        String msgId = msg.getSumy().getMsgId();
        String sendTime = msg.getSumy().getSendTime();
        String senderName = msg.getSumy().getSenderName();
        String fileName = msg.getSumy().getFileName();
        String receiverId = msg.getSumy().getReceiverId();

        if (Globle.REQUEST.equals(interactMode)) {
            MsgLog msgLog = new MsgLog();
            msgLog.msgId = msgId;
            msgLog.msgTime = sendTime;
            msgLog.sendNumber = sendId;
            msgLog.nickName = senderName;

            if (Globle.GROUP.equals(chatMode)) {
                msgLog.chatMode = MsgLog.CHAT_MODE_GROUP;
                msgLog.receiveNumber = groupId;
            }
            if (Globle.PRIVATE.equals(chatMode)) {
                msgLog.chatMode = MsgLog.CHAT_MODE_PRIVATE;
                msgLog.receiveNumber = receiverId;
            }

            if (Globle.TXT_MSG.equals(msgType)) {
                String body = msg.getBody().getString("txt");
                msgLog.msgType = MsgLog.TYPE_TXT_MSG;
                msgLog.msgContent = body;
            }
            EventBus.getDefault().post(new ChatMessage(Globle.KEY_EVENT_CHAT, msgLog));
        } else if (Globle.RESPONSE.equals(interactMode)) {
            // 发送消息之后的回执
            JSONObject jsonObj = msg.getBody();
            if (jsonObj.getString(Globle.EXESTATUS).equals("0") && jsonObj.getString(Globle.ERRCODE).equals("221")) {
                //被禁言
                String string = jsonObj.getString(Globle.ERRMSG);
                if (!TextUtils.isEmpty(string)) {
                    MsgLog msgLog = new MsgLog();
                    msgLog.setMsgType(MsgLog.TYPE_SYSTEM);
                    msgLog.msgContent = string;
                    EventBus.getDefault().post(new ChatMessage(Globle.KEY_EVENT_CHAT, msgLog));
                }
            }
        }
    }

    /**
     * 礼物消息
     *
     * @param msg
     */
    public void onRecvGifMsg(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.REQUEST.equals(interactMode)) {
            String giftId = msg.getBody().getString("giftId");
            GiftListResult.GiftListEntity giftEntity = GiftUtils.getGiftById(giftId);
            ReciveGiftInfo giftInfo = null;
            ReciveGiftInfo giftMsg = null;
            if (giftEntity != null) {
                giftInfo = new ReciveGiftInfo();

                giftInfo.setSenderName(msg.getBody().getString("senderName"));
                giftInfo.setSenderId(msg.getBody().getString("senderId"));
                giftInfo.setGiftId(giftId);
                giftInfo.setReceiverId(msg.getBody().getString("receiverId"));
                giftInfo.setReceiverName(msg.getBody().getString("receiverName"));
                giftInfo.setGiftCnt(Integer.parseInt(msg.getBody().getString("cnt")));
                giftInfo.setGiftName(giftEntity.getName());
                giftInfo.setGiftUrl(giftEntity.getPic());

//                if ("1".equals(giftEntity.getType())) {
                    giftInfo.setId(Globle.KEY_EVENT_RECEIVE_SPECIAL_GIFT);
//                } else {
//                    giftInfo.setId(Globle.KEY_EVENT_RECEIVE_GIFT);
//                }
                EventBus.getDefault().post(giftInfo);
            }

            if (giftEntity != null) {
                giftMsg = new ReciveGiftInfo();

                giftMsg.setSenderName(msg.getBody().getString("senderName"));
                giftMsg.setSenderId(msg.getBody().getString("senderId"));
                giftMsg.setGiftId(giftId);
                giftMsg.setReceiverId(msg.getBody().getString("receiverId"));
                giftMsg.setReceiverName(msg.getBody().getString("receiverName"));
                giftMsg.setGiftCnt(Integer.parseInt(msg.getBody().getString("cnt")));
                giftMsg.setGiftName(giftEntity.getName());
                giftMsg.setGiftUrl(giftEntity.getPic());

                //用做聊天消息的生成
                giftMsg.setId(Globle.KEY_EVENT_RECEIVE__GIFT_FORCHAT);
                EventBus.getDefault().post(giftMsg);
            }

        } else {
            //回执
            String balance = msg.getBody().getString("balance");
            String string = msg.getBody().getString(Globle.EXESTATUS);
            if (!TextUtils.isEmpty(string) && string.equals("1")) {
                SendGiftResponse sendGiftResponse = new SendGiftResponse();
                sendGiftResponse.setBalance(balance);
                sendGiftResponse.setId(Globle.SEND_GIFT_RESPONSE);
                EventBus.getDefault().post(sendGiftResponse);
            }

        }
    }

    /**
     * 房间广播
     *
     * @param msg
     */
    public void onRecvRoomBroadcast(Message msg) {

        RoomBroadcastEvent roomBroadcastEvent = new RoomBroadcastEvent();

        roomBroadcastEvent.id = Globle.KEY_EVENT_ROOM_BROADCAST;
        roomBroadcastEvent.setMemberCnt(msg.getBody().getString("memberCnt"));
        roomBroadcastEvent.setSupportCnt(msg.getBody().getString("newSuptCnt"));
        roomBroadcastEvent.setTotalSupportCnt(msg.getBody().getString("totalSuptCnt"));
        roomBroadcastEvent.setIncome(msg.getBody().getString("income"));

        EventBus.getDefault().post(roomBroadcastEvent);
    }

    /**
     * 进场
     *
     * @param msg
     */
    public void onUserEnterRoomMsg(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.REQUEST.equals(interactMode)) {
            JSONObject body = msg.getBody();
            EnterRoomEvent enterRoomEvent = new EnterRoomEvent();
            String userId = body.getString("userId");
            String nickName = body.getString("nickName");
            String profile = body.getString("profile");
            String sex = body.getString("sex");

            enterRoomEvent.setUserId(userId);
            enterRoomEvent.setProfile(profile);
            enterRoomEvent.setSex(sex);
            enterRoomEvent.setNickName(nickName);
//            if(TextUtils.isEmpty(userId) || userId.equals("-1")){
//                enterRoomEvent.setNickName(AceApplication.getApplication().getString(R.string.youke));
//            }
            enterRoomEvent.setId(Globle.KEY_ENTER_ROOM_MSG);
            EventBus.getDefault().post(enterRoomEvent);

//            String groupId = msg.getSumy().getShowId();
//            String msgId = msg.getSumy().getMsgId();
//            String sendTime = msg.getSumy().getSendTime();
//            MsgLog msgLog = new MsgLog();
//            msgLog.msgId = msgId;
//            msgLog.msgTime = sendTime;
//            msgLog.sendNumber = userId;
//            msgLog.nickName = nickName;
//            msgLog.chatMode = MsgLog.CHAT_ENTER_ROOM;
//            msgLog.receiveNumber = groupId;
//            msgLog.msgType = MsgLog.MSG_TEXT_RECV;
//            EventBus.getDefault().post(new ChatMessage(Globle.KEY_EVENT_CHAT, msgLog));
        } else {
            JSONObject body = msg.getBody();
            String string = body.getString(Globle.EXESTATUS);
            String code = body.getString(Globle.ERRCODE);
            String content = body.getString(Globle.ERRMSG);
            if (!TextUtils.isEmpty(string) && string.equals("0")) {
                //已经停播
                if (Globle.KEY_FREEZE_ACCOUNT_CODE.equals(code)) {
                    //已经被封号
                    //EventBus.getDefault().post(new SealNumberEvent(Globle.KEY_EVENT_SEAL_NUMBER, content));
                } else {
                    BaseEvent event = new BaseEvent();
                    event.setId(Globle.KEY_LIVE_FINISH);
                    EventBus.getDefault().post(event);
                }
            }

        }
    }

    /**
     * 踢人消息
     *
     * @param msg
     */
    public void onKickMsg(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        KickOutEvent kickOutEvent = new KickOutEvent();
        if (Globle.REQUEST.equals(interactMode)) {
            kickOutEvent.setIsResponse(false);
            kickOutEvent.setUserId(msg.getBody().getString("userId"));
            kickOutEvent.setUserName(msg.getBody().getString("userName"));
        } else {
            kickOutEvent.setIsResponse(true);
            if (msg.getBody().getString(Globle.EXESTATUS).equals("1")) {
                kickOutEvent.setIsSuccess(true);
            }
        }
        kickOutEvent.setId(Globle.KEY_KICKOUT_MSG);
        EventBus.getDefault().post(kickOutEvent);
    }

    /**
     * 直播结束
     *
     * @param msg
     */
    public void onRecvLiveEnd(Message msg) {
        LogUtils.i("SessionPackParser", "onRecvLiveEnd response=" + JSON.toJSONString(msg));
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.REQUEST.equals(interactMode)) {
            BaseEvent event = new BaseEvent();
            event.setId(Globle.KEY_LIVE_FINISH);
            EventBus.getDefault().post(event);

        }
    }

    /**
     * 退出房间
     *
     * @param msg
     */
    public void onExitRoom(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.RESPONSE.equals(interactMode)) {
            int exeStatu = 0;
            String exeStatus = (String) msg.getBody().get("exeStatus");
            try {
                exeStatu = Integer.parseInt(exeStatus);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (exeStatu == 1) {
                String conins = (String) msg.getBody().get("coins");
                String fans = (String) msg.getBody().get("fans");
                String length = (String) msg.getBody().get("length");
                String memberCnt = (String) msg.getBody().get("memberCnt");
                String support = (String) msg.getBody().get("support");
                LiveEndEvent liveEndEvent = new LiveEndEvent();
                liveEndEvent.conins = conins;
                liveEndEvent.fans = fans;
                liveEndEvent.length = length;
                liveEndEvent.memberCnt = memberCnt;
                liveEndEvent.support = support;
                EventBus.getDefault().post(liveEndEvent);
            }
        } else {

        }
    }

    /**
     * 直播间警告消息
     *
     * @param msg
     */
    public void onRecvWarn(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        String chatMode = msg.getSumy().getChatMode();
        String msgType = msg.getSumy().getMsgType();
        String sendId = msg.getSumy().getSenderId();
        String groupId = msg.getSumy().getShowId();
        String msgId = msg.getSumy().getMsgId();
        String sendTime = msg.getSumy().getSendTime();
        String senderName = msg.getSumy().getSenderName();
        String receiverId = msg.getSumy().getReceiverId();

        if (Globle.REQUEST.equals(interactMode)) {
            MsgLog msgLog = new MsgLog();
            msgLog.msgId = msgId;
            msgLog.msgTime = sendTime;
            msgLog.sendNumber = sendId;
            msgLog.nickName = senderName;

            if (Globle.GROUP.equals(chatMode)) {
                msgLog.chatMode = MsgLog.CHAT_MODE_GROUP;
                msgLog.receiveNumber = groupId;
            }
            if (Globle.PRIVATE.equals(chatMode)) {
                msgLog.chatMode = MsgLog.CHAT_MODE_PRIVATE;
                msgLog.receiveNumber = receiverId;
            }

            if (Globle.TXT_MSG.equals(msgType)) {
                String body = msg.getBody().getString("txt");
                msgLog.msgType = MsgLog.TYPE_SYSTEM;
                msgLog.msgContent = body;
            }
            EventBus.getDefault().post(new ChatMessage(Globle.KEY_RECEIVE_WARN, msgLog));
        } else if (Globle.RESPONSE.equals(interactMode)) {

        }
    }

    /**
     * 收到房间消息：type:1,礼物；2，分享；3，点赞；4，系统广播，5，关注
     *
     * @param msg
     */
    public void onRecvRoomMsg(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        String chatMode = msg.getSumy().getChatMode();
        String msgType = msg.getSumy().getMsgType();
        String sendId = msg.getSumy().getSenderId();
        String groupId = msg.getSumy().getShowId();
        String msgId = msg.getSumy().getMsgId();
        String sendTime = msg.getSumy().getSendTime();
        String senderName = msg.getSumy().getSenderName();

        if (Globle.REQUEST.equals(interactMode)) {

            MsgLog msgLog = new MsgLog();
            msgLog.msgId = msgId;
            msgLog.msgTime = sendTime;
            msgLog.sendNumber = sendId;

            if (Globle.TXT_MSG.equals(msgType)) {
                String type = msg.getBody().getString("type");
                String cnt = msg.getBody().getString("cnt");
                String sysMsg = msg.getBody().getString("msg");
                String strNiackName = msg.getBody().getString("nickName");
                msgLog.nickName = strNiackName;
                String revMsg = "";
                if (Globle.TYOE_MSG_WARNING.equals(type)) { //警告消息

                    msgLog.msgType = MsgLog.TYPE_SYSTEM;
                    msgLog.msgContent = sysMsg;
                    msgLog.chatMode = MsgLog.CHAT_MODE_PRIVATE;
                    EventBus.getDefault().post(new ChatMessage(Globle.KEY_RECEIVE_WARN, msgLog));

                } else {

                    if (Globle.TYP_MSG_SHARE.equals(type)) {
                        msgLog.msgType = MsgLog.TYPE_SHARE;
                        String sShare = mContext.getString(R.string.sys_share_text);
                        revMsg = sShare;
                    } else if (Globle.TYP_MSG_SUPPORT.equals(type)) {

                        msgLog.msgType = MsgLog.TYPE_SUPPORT;
                        String sSupport = mContext.getString(R.string.sys_support_text, cnt);
                        revMsg = sSupport;

                    } else if (Globle.TYP_MSG_SYSTEM.equals(type)) {//系统消息

                        msgLog.msgType = MsgLog.TYPE_SYSTEM;
                        revMsg = sysMsg;

                    } else if (Globle.TYP_MSG_FOLLOW.equals(type)) {

                        msgLog.msgType = MsgLog.TYPE_FOLLOW;
                        String sFollow = mContext.getString(R.string.sys_follow_text);
                        revMsg = sFollow;
                    }
                    msgLog.msgContent = revMsg;
                    msgLog.chatMode = MsgLog.CHAT_MODE_GROUP;
                    if (!TextUtils.isEmpty(msgLog.nickName) && !TextUtils.isEmpty(msgLog.msgContent)) {
                        EventBus.getDefault().post(new ChatMessage(Globle.KEY_EVENT_CHAT, msgLog));
                    }

                }

            }

        }
    }

    /**
     * 禁播
     *
     * @param msg
     */
    public void liveForbid(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.REQUEST.equals(interactMode)) {
            String body = msg.getBody().getString("txt");
            String userId = msg.getBody().getString("userId");
            if (UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo().getUserId().equals(userId)) {
                // EventBus.getDefault().post(new ForBidEvent(Globle.KEY_EVENT_LIVE_BAN, body));
            }
        }
    }

    /**
     * 封号
     *
     * @param msg
     */
    public void sealNumber(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.REQUEST.equals(interactMode)) {
            String body = msg.getBody().getString("txt");
            String userId = msg.getBody().getString("userId");
            if (UserInfoUtils.isAlreadyLogin() && UserInfoUtils.getUserInfo().getUserId().equals(userId)) {
                EventBus.getDefault().post(new SealNumberEvent(Globle.KEY_EVENT_SEAL_NUMBER, body));
            }
        }
    }

    /**
     * 直播暂停
     *
     * @param msg
     */
    public void onLivePause(Message msg) {
        String interactMode = msg.getSumy().getInteractMode();
        if (Globle.REQUEST.equals(interactMode)) {
            String type = msg.getBody().getString("type");
            LivePauseEvent event;
            if ("0".equals(type)) {
                //暂停
                event = new LivePauseEvent(Globle.KEY_EVENT_LIVE_PAUSE, true);
                EventBus.getDefault().post(event);
            } else if ("1".equals(type)) {
                //恢复
                event = new LivePauseEvent(Globle.KEY_EVENT_LIVE_PAUSE, false);
                EventBus.getDefault().post(event);
            }
        }
    }
}
