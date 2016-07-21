package com.rednovo.ace.data.cell;

/**
 * Created by lizhen on 16/3/11.
 */
public class MsgLog extends BaseMode {

    /**
     *
     */
    private static final long serialVersionUID = 8153076376552307439L;

    /**
     * 文本消息的接收和发送
     **/
    public static final int MSG_TEXT_RECV = 1;
    public static final int MSG_TEXT_SEND = 2;

    /**
     * 语音消息发送接收的类型
     **/
    public static final int MSG_VOICE_RECV = 3;
    public static final int MSG_VOICE_SEND = 4;

    /**
     * 图片消息发送接收的类型
     **/
    public static final int MSG_PIC_RECV = 6;
    public static final int MSG_PIC_SEND = 7;


    /**
     * 消息发送的状态
     **/
    public static final int STATUS_SEND = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 2;

    /**
     * 消息未读状态
     **/
    public static final int STATUS_READ = 3;
    public static final int STATUS_UNREAD = 4;

    /**
     * 与封装CHATMODE类型是对应的
     **/
    public static final int CHAT_MODE_PRIVATE = 0;// 私聊
    public static final int CHAT_MODE_GROUP = 1;// 群聊

    //消息类型
    /**普通文本消息*/
    public static final int TYPE_TXT_MSG = 1;
    /**系统消息*/
    public static final int TYPE_SYSTEM = 2;
    /**直播间提醒*/
    public static final int TYPE_TIPS = 5;
    /**送礼物消息*/
    public static final int TYPE_GIFT = 6;
    /**分享消息*/
    public static final int TYPE_SHARE = 7;
    /**点赞*/
    public static final int TYPE_SUPPORT = 8;
    /**关注*/
    public static final int TYPE_FOLLOW = 9;

    /**
     * 1文本 2是私聊语音 3是群聊语音
     */
    public int msgType;
    public int msgDuration;
    public String msgTime;
    public int chatMode;
    public String msgId;
    public String receiveNumber;
    public String showId;
    public String sendNumber;
    public String nickName;
    public String msgContent;


    public boolean isRecv() {
        return (msgType == MSG_TEXT_RECV) || (msgType == MSG_VOICE_RECV) || (msgType == MSG_PIC_RECV);
    }

    public boolean isSend() {
        return !isRecv();
    }

    public boolean isText() {
        return (msgType == MSG_TEXT_SEND) || (msgType == MSG_TEXT_RECV);
    }

    public boolean isVoice() {
        return (msgType == MSG_VOICE_SEND) || (msgType == MSG_VOICE_RECV);
    }

    public boolean isPic() {
        return (msgType == MSG_PIC_RECV) || (msgType == MSG_PIC_SEND);
    }


    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSendNumber() {
        return sendNumber;
    }

    public void setSendNumber(String sendNumber) {
        this.sendNumber = sendNumber;
    }

    public String getReceiveNumber() {
        return receiveNumber;
    }

    public void setReceiveNumber(String receiveNumber) {
        this.receiveNumber = receiveNumber;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getChatMode() {
        return chatMode;
    }

    public void setChatMode(int chatMode) {
        this.chatMode = chatMode;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public int getMsgDuration() {
        return msgDuration;
    }

    public void setMsgDuration(int msgDuration) {
        this.msgDuration = msgDuration;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
