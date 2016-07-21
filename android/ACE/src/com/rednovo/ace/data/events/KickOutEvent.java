package com.rednovo.ace.data.events;

/**
 * 踢人消息
 */
public class KickOutEvent extends BaseEvent{
    boolean isResponse;
    boolean isSuccess;

    /**
     * 被踢人
     */
    String userId;
    String userName;

    public boolean isResponse() {
        return isResponse;
    }

    public void setIsResponse(boolean isResponse) {
        this.isResponse = isResponse;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
