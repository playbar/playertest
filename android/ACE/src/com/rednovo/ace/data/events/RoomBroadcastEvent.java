package com.rednovo.ace.data.events;

/**
 * Created by lilong on 16/3/14.
 */
public class RoomBroadcastEvent extends BaseEvent{

    private String memberCnt;

    private String totalSupportCnt;

    private String supportCnt;

    private String income;


    public String getMemberCnt() {
        return memberCnt;
    }

    public void setMemberCnt(String memberCnt) {
        this.memberCnt = memberCnt;
    }

    public String getTotalSupportCnt() {
        return totalSupportCnt;
    }

    public void setTotalSupportCnt(String totalSupportCnt) {
        this.totalSupportCnt = totalSupportCnt;
    }

    public String getSupportCnt() {
        return supportCnt;
    }

    public void setSupportCnt(String supportCnt) {
        this.supportCnt = supportCnt;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }
}
