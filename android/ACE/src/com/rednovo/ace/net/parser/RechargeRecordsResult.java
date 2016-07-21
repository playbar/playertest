package com.rednovo.ace.net.parser;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dk on 16/3/10.
 */
public class RechargeRecordsResult extends BaseResult{

    /**
     * amount : 1500000
     * channel : 1
     * createTime : 2016-03-10 22:35:40
     * desciption : 用户4233给用户4233添加金币1500000.0 [订单充值]
     * id : 3
     * relateUserId : 4233
     * relateUserName :
     * userId : 4233
     * userName :
     */

    private List<AccountListEntity> accountList;

    public void setAccountList(List<AccountListEntity> accountList) {
        this.accountList = accountList;
    }

    public List<AccountListEntity> getAccountList() {
        return accountList;
    }

    public static class AccountListEntity implements Serializable{
        private int amount;
        private String channel;
        private String createTime;
        private String desciption;
        private String id;
        private String relateUserId;
        private String relateUserName;
        private String userId;
        private String userName;

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setDesciption(String desciption) {
            this.desciption = desciption;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setRelateUserId(String relateUserId) {
            this.relateUserId = relateUserId;
        }

        public void setRelateUserName(String relateUserName) {
            this.relateUserName = relateUserName;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getAmount() {
            return amount;
        }

        public String getChannel() {
            return channel;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getDesciption() {
            return desciption;
        }

        public String getId() {
            return id;
        }

        public String getRelateUserId() {
            return relateUserId;
        }

        public String getRelateUserName() {
            return relateUserName;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }
}
