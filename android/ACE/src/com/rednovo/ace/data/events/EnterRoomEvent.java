package com.rednovo.ace.data.events;

/**
 * 进入房间的广播
 */
public class EnterRoomEvent extends BaseEvent{
    String userId;
    String nickName;
    String profile;
    String sex;

    public EnterRoomEvent() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public EnterRoomEvent(String userId, String nickName, String profile, String sex) {
        this.userId = userId;
        this.nickName = nickName;
        this.profile = profile;
        this.sex = sex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
