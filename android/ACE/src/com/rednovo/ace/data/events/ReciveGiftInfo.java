package com.rednovo.ace.data.events;

/**
 * Created by lilong on 16/3/13.
 */
public class ReciveGiftInfo extends BaseEvent{

    private String senderName;

    private String senderId;

    private String receiverId;

    private String receiverName;

    private String giftId;

    private String giftName;

    private int giftCnt;

    private String giftUrl;

    public void setGiftCnt(int cnt) {
        giftCnt = cnt;
    }

    public int getGiftCnt() {
        return giftCnt;
    }

    public void setGiftUrl(String url) {
        this.giftUrl = url;
    }

    public String getGiftUrl() {
        return giftUrl;
    }

    public ReciveGiftInfo(int key) {
        id = key;
    }

    public ReciveGiftInfo() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }
}
