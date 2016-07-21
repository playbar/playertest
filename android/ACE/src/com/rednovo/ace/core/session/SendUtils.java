package com.rednovo.ace.core.session;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.rednovo.ace.common.Globle;
import com.rednovo.ace.common.KeyGenerator;
import com.rednovo.ace.communication.client.ClientSession;
import com.rednovo.ace.data.cell.MsgLog;
import com.rednovo.ace.data.events.ReciveGiftInfo;
import com.rednovo.ace.entity.Message;
import com.rednovo.ace.entity.Summary;
import com.rednovo.libs.ui.base.BasicData;

import de.greenrobot.event.EventBus;

/**
 * Created by lizhen on 16/3/11.
 */
public class SendUtils {


    /**
     * 发送私聊文本
     *
     * @param sendId
     * @param recvId
     * @param txtMSG
     */
    private void sendPrivateText(String sendId, String recvId, String txtMSG) {
        sendChat(Globle.TXT_MSG, Globle.PRIVATE, sendId, recvId, "", txtMSG);
    }

    /**
     * 发送群聊文本
     *
     * @param sendId
     * @param recvId 群组聊天可以recvId不用传入，也可以传入GroupID
     * @param showId
     * @param txtMSG 文本
     */
    private static void sendGroupText(String sendId, String recvId, String showId, String txtMSG) {
        sendChat(Globle.TXT_MSG, Globle.GROUP, sendId, recvId, showId, txtMSG);
    }

    /**
     * 发送私聊语音
     *
     * @param sendId   发送者ID
     * @param recvId   接受者ID
     * @param fileName 语音文件路径
     */
    private static void sendPrivateVoice(String sendId, String recvId, String fileName, String duration) {
        sendVoice(Globle.MEDIA_MSG_AUDIO, Globle.PRIVATE, sendId, recvId, "", fileName, duration);

    }

    /**
     * 发送群聊语音
     *
     * @param sendId
     * @param recvId   群组聊天可以recvId不用传入，也可以传入GroupID
     * @param groupId
     * @param fileName
     */
    private static void sendGroupVoice(String sendId, String recvId, String groupId, String fileName, String duration) {
        sendVoice(Globle.MEDIA_MSG_AUDIO, Globle.GROUP, sendId, recvId, groupId, fileName, duration);
    }

    /**
     * 发送私聊图片
     *
     * @param sendId
     * @param recvId
     * @param fileName
     * @author zhen.Li
     * @since 2015-5-20下午6:57:17
     */
    private static void sendPrivatePic(String sendId, String recvId, String fileName) {
        sendPictrue(Globle.MEDIA_MSG_PIC, Globle.PRIVATE, sendId, recvId, "", fileName);
    }

    /**
     * 发送群组图片
     *
     * @param sendId
     * @param recvId
     * @param groupId
     * @param fileName
     * @author zhen.Li
     * @since 2015-5-20下午6:57:26
     */
    private static void sendGroupPic(String sendId, String recvId, String groupId, String fileName) {
        sendPictrue(Globle.MEDIA_MSG_PIC, Globle.GROUP, sendId, recvId, groupId, fileName);
    }

    /**
     * 发送图片
     *
     * @param msgType
     * @param chatMode
     * @param sendId
     * @param recvId
     * @param showId
     * @param fileName
     * @author zhen.Li
     * @since 2015-5-20下午6:51:46
     */
    private static void sendPictrue(String msgType, String chatMode, String sendId, String recvId, String showId, String fileName) {
        Message msg = new Message();
        Summary sumy = new Summary();

        sumy.setRequestKey(Globle.KEY_SEND_MSG_STATUS);
        sumy.setChatMode(chatMode);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(msgType);
        sumy.setReceiverId(recvId);
        sumy.setSenderId(sendId);
        sumy.setFileName(fileName);
        if (!TextUtils.isEmpty(showId)) {
            sumy.setShowId(showId);
        }
        msg.setSumy(sumy);
        ClientSession.getInstance().sendMessage(msg);
    }

    /**
     * @param msgType  消息类型
     * @param chatMode 聊天模式
     * @param sendId   发送者id
     * @param recvId   接受者id
     * @param groupId  群组id
     * @param fileName 文件路径
     */
    private static void sendVoice(String msgType, String chatMode, String sendId, String recvId, String groupId, String fileName, String duration) {
        Message msg = new Message();
        Summary sumy = new Summary();

        sumy.setRequestKey(Globle.KEY_SEND_MSG_STATUS);
        sumy.setChatMode(chatMode);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(msgType);
        sumy.setReceiverId(recvId);
        sumy.setSenderId(sendId);
        sumy.setFileName(fileName);
        sumy.setDuration(TextUtils.isEmpty(duration) ? "0" : duration);
        if (!TextUtils.isEmpty(groupId)) {
            sumy.setShowId(groupId);
        }
        msg.setSumy(sumy);
        ClientSession.getInstance().sendMessage(msg);
    }

    /**
     * @param chatMode 私聊，还是群聊，目前封装在ChatMode类中
     * @param msgType  消息类型，Globle定义的常量类型
     * @param sendId   发送者ID
     * @param recvId   接收者id
     * @param showId   群组ID
     * @param txtMSG   消息文本
     */
    private static void sendChat(String msgType, String chatMode, String sendId, String recvId, String showId, String txtMSG) {
        Message msg = new Message();
        Summary sumy = new Summary();

        sumy.setRequestKey(Globle.KEY_SEND_MSG_STATUS);
        sumy.setChatMode(chatMode);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(msgType);
        sumy.setReceiverId(recvId);
        sumy.setSenderId(sendId);
        if (!TextUtils.isEmpty(showId)) {
            sumy.setShowId(showId);
        }
        msg.setSumy(sumy);
        JSONObject obj = new JSONObject();
        obj.put("basicData", BasicData.formatJson());
        obj.put("senderId", sendId);
        obj.put("showId", showId);
        obj.put("receiverId", recvId);
        obj.put("chatMode", chatMode);
        obj.put("txt", txtMSG);
        msg.setBody(obj);

        ClientSession.getInstance().sendMessage(msg);
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public static void sendMessage(MsgLog msg) {
        if (msg != null) {
//            int chatMode = msg.chatMode;
//            if (chatMode == MsgLog.CHAT_MODE_PRIVATE) {
//                // if (msg.isText()) {
//                sendPrivateText(msg.sendNumber, msg.receiveNumber, msg.msgContent);
            // }
            // if (msg.isVoice()) {
            // sendPrivateVoice(msg.sendNumber, msg.receiveNumber, msg.msgContent, String.valueOf(msg.msgDuration));
            // }
            // if (msg.isPic()) {
            // sendPrivatePic(msg.sendNumber, msg.receiveNumber, msg.msgContent);
            // }
            // } else {
            // if (msg.isText()) {
            sendGroupText(msg.sendNumber, msg.receiveNumber, msg.showId, msg.msgContent);
            //}
            // if (msg.isVoice()) {
            // sendGroupVoice(msg.sendNumber, msg.receiveNumber, msg.receiveNumber, msg.msgContent, String.valueOf(msg.msgDuration));
            // }
            // if (msg.isPic()) {
            // sendGroupPic(msg.sendNumber, msg.receiveNumber, msg.receiveNumber, msg.msgContent);
            // }
//        }

        }

    }

    /**
     * 进入房间
     */
    public static void enterRoom(String userId, String showId) {
        Message msg = new Message();
        Summary sumy = new Summary();
        sumy.setRequestKey(Globle.KEY_ENTER_ROOM);
        sumy.setChatMode(Globle.GROUP);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(Globle.TXT_MSG);
        if (TextUtils.isEmpty(userId)) {
            sumy.setSenderId("-1");
        } else {
            sumy.setSenderId(userId);
        }
        msg.setSumy(sumy);

        JSONObject json = new JSONObject();
        json.put("basicData", BasicData.formatJson());
        json.put("userId", userId);
        json.put("showId", showId);
        msg.setBody(json);
        ClientSession.getInstance().sendMessage(msg);
    }


    /**
     * 退出房间
     *
     * @param userId
     * @param showId
     */
    public static void exitRoom(String userId, String showId) {
        Message msg = new Message();
        Summary sumy = new Summary();
        sumy.setRequestKey(Globle.KEY_EXIT_ROOM);
        sumy.setChatMode(Globle.GROUP);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(Globle.TXT_MSG);
        if (TextUtils.isEmpty(userId)) {
            sumy.setSenderId("-1");
        } else {
            sumy.setSenderId(userId);
        }
        msg.setSumy(sumy);

        JSONObject json = new JSONObject();
        json.put("basicData", BasicData.formatJson());
        json.put("userId", userId);
        json.put("showId", showId);
        msg.setBody(json);
        ClientSession.getInstance().sendMessage(msg);
    }

    /**
     * 踢人
     *
     * @param starId
     * @param userId
     * @param showId
     */
    public static void kickOut(String starId, String userId, String showId) {
        Message msg = new Message();
        Summary sumy = new Summary();
        sumy.setRequestKey(Globle.KEY_KICK_OUT);
        sumy.setChatMode(Globle.GROUP);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(Globle.TXT_MSG);
        if (TextUtils.isEmpty(userId)) {
            sumy.setSenderId("-1");
        } else {
            sumy.setSenderId(userId);
        }
        msg.setSumy(sumy);

        JSONObject json = new JSONObject();
        json.put("basicData", BasicData.formatJson());
        json.put("starId", starId);
        json.put("userId", userId);
        json.put("showId", showId);
        msg.setBody(json);
        ClientSession.getInstance().sendMessage(msg);
    }

    /**
     * 禁言
     * @param starId
     * @param userId
     * @param showId
     */
    public static void shutup(String starId, String userId, String showId) {
        Message msg = new Message();
        Summary sumy = new Summary();
        sumy.setRequestKey(Globle.KEY_SHUTUP);
        sumy.setChatMode(Globle.GROUP);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(Globle.TXT_MSG);
        if (TextUtils.isEmpty(starId)) {
            sumy.setSenderId("-1");
        } else {
            sumy.setSenderId(starId);
        }
        msg.setSumy(sumy);

        JSONObject json = new JSONObject();
        json.put("basicData", BasicData.formatJson());
        json.put("starId", starId);
        json.put("userId", userId);
        json.put("showId", showId);
        msg.setBody(json);
        ClientSession.getInstance().sendMessage(msg);
    }


    /**
     * 送礼物
     *
     * @param senderId
     * @param receiverId
     * @param showId
     * @param giftId
     * @param cnt
     */
    public static void sendGif(String senderId, String receiverId, String showId, String giftId, String cnt) {
        Message msg = new Message();
        Summary sumy = new Summary();
        sumy.setRequestKey(Globle.KEY_SEND_GIF);
        sumy.setChatMode(Globle.GROUP);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        sumy.setMsgType(Globle.TXT_MSG);
        if (TextUtils.isEmpty(senderId)) {
            sumy.setSenderId("-1");
        } else {
            sumy.setSenderId(senderId);
        }
        msg.setSumy(sumy);
        JSONObject json = new JSONObject();
        json.put("basicData", BasicData.formatJson());
        json.put("senderId", senderId);
        json.put("receiverId", receiverId);
        json.put("showId", showId);
        json.put("giftId", giftId);
        json.put("cnt", cnt);
        msg.setBody(json);
        ClientSession.getInstance().sendMessage(msg);
    }


    /**
     * 发送点赞
     *
     * @param showId
     * @param userId
     * @param cnt
     */
    public static void sendParise(String showId, String userId, String cnt) {
        Message msg = new Message();
        Summary sumy = new Summary();
        sumy.setRequestKey(Globle.KEY_SEND_PARISE);
        sumy.setChatMode(Globle.GROUP);
        sumy.setInteractMode(Globle.REQUEST);
        sumy.setMsgId(KeyGenerator.createUniqueId());
        if (TextUtils.isEmpty(userId)) {
            sumy.setSenderId("-1");
        } else {
            sumy.setSenderId(userId);
        }
        sumy.setMsgType(Globle.TXT_MSG);
        msg.setSumy(sumy);

        JSONObject json = new JSONObject();
        json.put("basicData", BasicData.formatJson());
        json.put("showId", showId);
        json.put("userId", userId);
        json.put("cnt", cnt);
        msg.setBody(json);
        ClientSession.getInstance().sendMessage(msg);
    }

}
